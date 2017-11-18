package ru.spbau.mit

import java.io.OutputStream

interface Element {
    fun render(builder: StringBuilder)
}

class TextElement(private val text: String) : Element {
    override fun render(builder: StringBuilder) {
        builder.append("$text\n")
    }
}

@DslMarker
annotation class TexMarker

@TexMarker
interface BaseElement : Element {

    /**
     * Introduced since functions (i.e. \frac{}{}) can have more than one pair of braces.
     */
    fun StringBuilder.addArgs(args: List<String>) {
        if (args.isNotEmpty()) {
            append(args.joinToString("}{", "{", "}"))
        }
    }

    fun StringBuilder.addExtraArgs(extraArgs: Array<out String>) {
        if (extraArgs.isNotEmpty()) {
            append(extraArgs.joinToString(",", "[", "]"))
        }
    }

    fun StringBuilder.nextLine() {
        append('\n')
    }

}

/**
 * Command of type
 * \name[...]{...}
 */
@TexMarker
abstract class InlineCommand(
        private val name: String,
        private val args: List<String>,
        private vararg val extraArgs: String
) : BaseElement {

    override fun render(builder: StringBuilder) {
        builder.append("\\$name")
        builder.addExtraArgs(extraArgs)
        builder.addArgs(args)
        builder.nextLine()
    }

}

/**
 * Common logic for all commands that can include other commands or text.
 */
@TexMarker
abstract class BaseContentCommand(
        open val name: String,
        open val args: List<String>,
        open vararg val extraArgs: String
) : BaseElement {

    private val children = arrayListOf<Element>()

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

    private fun renderBegin(builder: StringBuilder) {
        builder.append("\\begin{$name}")
        builder.addExtraArgs(extraArgs)
        builder.addArgs(args)
        builder.nextLine()
    }

    fun renderChildren(builder: StringBuilder) {
        children.forEach { it.render(builder) }
    }

    private fun renderEnd(builder: StringBuilder) {
        builder.append("\\end{$name}")
        builder.nextLine()
    }

    override fun render(builder: StringBuilder) {
        renderBegin(builder)
        renderChildren(builder)
        renderEnd(builder)
    }

    protected fun <T : Element> initElement(element: T, init: T.() -> Unit): T {
        element.init()
        children.add(element)
        return element
    }

    private fun addInlineCommand(command: InlineCommand) {
        children.add(command)
    }

    fun customInlineTag(name: String, args: List<String>, vararg extraArgs: String)
            = addInlineCommand(CustomInlineTag(name, args, *extraArgs))

    fun left(init: Left.() -> Unit) = initElement(Left(), init)

    fun center(init: Center.() -> Unit) = initElement(Center(), init)

    fun right(init: Right.() -> Unit) = initElement(Right(), init)
}

/**
 * Command of type
 * \begin{name}[...]
 * ...
 * \end{name}
 */
@TexMarker
abstract class BlockCommand(
        override val name: String,
        override val args: List<String>,
        override vararg val extraArgs: String
) : BaseContentCommand(name, args, *extraArgs) {

    fun frame(name: String, vararg extraArgs: String, init: Frame.() -> Unit)
            = initElement(Frame(name, *extraArgs), init)

    fun enumerate(vararg extraArgs: String, init: Enumerate.() -> Unit) = initElement(Enumerate(*extraArgs), init)

    fun itemize(vararg extraArgs: String, init: Itemize.() -> Unit) = initElement(Itemize(*extraArgs), init)

    fun math(init: Math.() -> Unit) = initElement(Math(), init)

    fun customBlockTag(name: String, vararg extraArgs: String, init: CustomBlockTag.() -> Unit)
            = initElement(CustomBlockTag(name, *extraArgs), init)
}

/**
 * Command of type
 * \begin{name}[...]
 * \item
 *  ...
 * \item
 *  ...
 * \end{name}
 */
@TexMarker
abstract class ListCommand(
        override val name: String,
        override val args: List<String>,
        override vararg val extraArgs: String
) : BaseContentCommand(name, args, *extraArgs) {

    fun item(init: Item.() -> Unit) = initElement(Item(), init)

}

class Math : BaseContentCommand("", emptyList()) {

    override fun render(builder: StringBuilder) {
        builder.append("$$\n")
        renderChildren(builder)
        builder.append("$$\n")
    }

}

class Item : BlockCommand("", emptyList()) {

    override fun render(builder: StringBuilder) {
        builder.append("\\item\n")
        renderChildren(builder)
    }

}

class Left : BlockCommand("flushleft", emptyList())

class Center : BlockCommand("center", emptyList())

class Right : BlockCommand("flushright", emptyList())

class DocumentClass(
        documentClass: String,
        vararg extraArgs: String
) : InlineCommand("documentclass", listOf(documentClass), *extraArgs)

class Package(
        packageName: String,
        vararg extraArgs: String
) : InlineCommand("usepackage", listOf(packageName), *extraArgs)

class Document : BlockCommand("document", emptyList()) {

    private var documentClass: DocumentClass? = null

    private val packages = arrayListOf<Package>()

    override fun render(builder: StringBuilder) {
        if (documentClass == null) throw TexBuilderException("Document should have a document class.")
        documentClass?.render(builder)
        packages.forEach { it.render(builder) }
        super.render(builder)
    }

    fun documentclass(className: String, vararg extraArgs: String) {
        if (documentClass != null) throw TexBuilderException("Document should have only one document class.")
        documentClass = DocumentClass(className, *extraArgs)
    }

    fun usepackage(packageName: String, vararg extraArgs: String) {
        packages.add(Package(packageName, *extraArgs))
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        render(stringBuilder)
        return stringBuilder.toString()
    }

    fun toOutputStream(stream: OutputStream) {
        stream.write(toString().toByteArray())
    }
}

class Enumerate(vararg extraArgs: String) : ListCommand("enumerate", emptyList(), *extraArgs)

class Itemize(vararg extraArgs: String) : ListCommand("itemize", emptyList(), *extraArgs)

class Frame(
        frameTitle: String,
        vararg extraArgs: String
) : BlockCommand("frame", listOf(frameTitle), *extraArgs)

class CustomBlockTag(name: String, vararg extraArgs: String) : BlockCommand(name, emptyList(), *extraArgs)

class CustomInlineTag(name: String, args: List<String>, vararg extraArgs: String) : InlineCommand(name, args, *extraArgs)

fun document(init: Document.() -> Unit): Document {
    val doc = Document()
    doc.init()
    return doc
}
