package ru.spbau.mit

import java.io.OutputStream

interface Renderer {
    fun append(string: String)
}

class StringBuilderRenderer(private val target: StringBuilder) : Renderer {
    override fun append(string: String) {
        target.append(string)
    }
}

class OutputStreamRenderer(private val target: OutputStream) : Renderer {
    override fun append(string: String) {
        target.write(string.toByteArray())
    }
}

@DslMarker
annotation class TexMarker

@TexMarker
interface Element {
    fun render(target: Renderer)
}

class TextElement(private val text: String) : Element {
    override fun render(target: Renderer) {
        target.append("$text\n")
    }
}

abstract class BaseElement : Element {

    /**
     * Introduced since functions (i.e. \frac{}{}) can have more than one pair of braces.
     */
    protected fun Renderer.addArgs(args: List<String>) {
        if (args.isNotEmpty()) {
            append(args.joinToString("}{", "{", "}"))
        }
    }

    protected fun Renderer.addExtraArgs(extraArgs: Array<out String>) {
        if (extraArgs.isNotEmpty()) {
            append(extraArgs.joinToString(",", "[", "]"))
        }
    }

    protected fun Renderer.nextLine() {
        append("\n")
    }

}

/**
 * Command of type
 * \name[...]{...}
 */
abstract class InlineCommand(
        private val name: String,
        private val args: List<String>,
        private vararg val extraArgs: String
) : BaseElement() {

    override fun render(target: Renderer) {
        target.append("\\$name")
        target.addExtraArgs(extraArgs)
        target.addArgs(args)
        target.nextLine()
    }

}

/**
 * Common logic for all commands that can include other commands or text.
 */
abstract class BaseContentCommand(
        private val name: String,
        private val args: List<String>,
        private vararg val extraArgs: String
) : BaseElement() {

    private val children = arrayListOf<Element>()

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

    private fun renderBegin(target: Renderer) {
        target.append("\\begin{$name}")
        target.addExtraArgs(extraArgs)
        target.addArgs(args)
        target.nextLine()
    }

    protected fun renderChildren(target: Renderer) {
        children.forEach { it.render(target) }
    }

    private fun renderEnd(target: Renderer) {
        target.append("\\end{$name}")
        target.nextLine()
    }

    override fun render(target: Renderer) {
        renderBegin(target)
        renderChildren(target)
        renderEnd(target)
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
abstract class BlockCommand(
        name: String,
        args: List<String>,
        vararg extraArgs: String
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
abstract class ListCommand(
        name: String,
        args: List<String>,
        vararg extraArgs: String
) : BaseContentCommand(name, args, *extraArgs) {

    fun item(init: Item.() -> Unit) = initElement(Item(), init)

}

class Math : BaseContentCommand("", emptyList()) {

    override fun render(target: Renderer) {
        target.append("$$\n")
        renderChildren(target)
        target.append("$$\n")
    }

}

class Item : BlockCommand("", emptyList()) {

    override fun render(target: Renderer) {
        target.append("\\item\n")
        renderChildren(target)
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

    override fun render(target: Renderer) {
        if (documentClass == null) throw TexBuilderException("Document should have a document class.")
        documentClass?.render(target)
        packages.forEach { it.render(target) }
        super.render(target)
    }

    fun documentclass(className: String, vararg extraArgs: String) {
        if (documentClass != null) throw TexBuilderException("Document should have only one document class.")
        documentClass = DocumentClass(className, *extraArgs)
    }

    fun usepackage(packageName: String, vararg extraArgs: String) {
        packages.add(Package(packageName, *extraArgs))
    }

    override fun toString() = buildString {
        render(StringBuilderRenderer(this))
    }

    fun toOutputStream(stream: OutputStream) {
        render(OutputStreamRenderer(stream))
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

fun document(init: Document.() -> Unit) = Document().apply(init)
