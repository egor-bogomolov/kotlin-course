package ru.spbau.mit

interface Element {
    fun render(builder: StringBuilder, indent: String)
}

class TextElement(private val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
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

    fun StringBuilder.addAdditionalArgs(additionalArgs: Array<out String>) {
        if (additionalArgs.isNotEmpty()) {
            append(additionalArgs.joinToString(",", "[", "]"))
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
        val name: String,
        val args: List<String>,
        vararg val additionalArgs: String
) : BaseElement {

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("\\$name")
        builder.addAdditionalArgs(additionalArgs)
        builder.addArgs(args)
        builder.nextLine()
    }

}

/**
 * Common logic of block and list commands.
 */
@TexMarker
abstract class BaseContentCommand(
        open val name: String,
        open val args: List<String>,
        open vararg val additionalArgs: String
) : BaseElement {

    val children = arrayListOf<Element>()

    private fun renderBegin(builder: StringBuilder, indent: String) {
        builder.append("\\begin{$name}")
        builder.addAdditionalArgs(additionalArgs)
        builder.addArgs(args)
        builder.nextLine()
    }

    fun renderChildren(builder: StringBuilder, indent: String) {
        children.forEach { it.render(builder, indent) }
    }

    private fun renderEnd(builder: StringBuilder, indent: String) {
        builder.nextLine()
        builder.append("\\end{$name}")
        builder.nextLine()
    }

    override fun render(builder: StringBuilder, indent: String) {
        renderBegin(builder, indent)
        renderChildren(builder, indent)
        renderEnd(builder, indent)
    }

    protected fun <T : Element> initElement(element: T, init: T.() -> Unit): T {
        element.init()
        children.add(element)
        return element
    }
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
        override vararg val additionalArgs: String
) : BaseContentCommand(name, args, *additionalArgs) {

    fun frame(name: String, vararg additionalArgs: String, init: Frame.() -> Unit)
            = initElement(Frame(name, *additionalArgs), init)

    fun enumerate(vararg additionalArgs: String, init: Enumerate.() -> Unit)
            = initElement(Enumerate(*additionalArgs), init)

    fun itemize(vararg additionalArgs: String, init: Itemize.() -> Unit) = initElement(Itemize(*additionalArgs), init)
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
        override vararg val additionalArgs: String
) : BaseContentCommand(name, args, *additionalArgs) {

    fun item(init: Item.() -> Unit) = initElement(Item(), init)

}


class Item : BlockCommand("item", emptyList()) {

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("\\item ")
        renderChildren(builder, indent)
    }

}

class Package(
        packageName: String,
        vararg additionalArgs: String
) : InlineCommand("usepackage", listOf(packageName), *additionalArgs)

class DocumentClass(
        documentClass: String,
        vararg additionalArgs: String
) : InlineCommand("documentclass", listOf(documentClass), *additionalArgs)

class Document : BlockCommand("document", emptyList()) {

    private var documentClass: DocumentClass? = null

    private val packages = arrayListOf<Package>()

    override fun render(builder: StringBuilder, indent: String) {
        documentClass?.render(builder, indent)
        packages.forEach { it.render(builder, indent) }
        super.render(builder, indent)
    }

    fun documentClass(className: String, vararg additionalArgs: String) {
        if (documentClass != null) throw Exception()
        documentClass = DocumentClass(className, *additionalArgs)
    }

    fun usepackage(packageName: String, vararg additionalArgs: String) {
        packages.add(Package(packageName, *additionalArgs))
    }

}

class Enumerate(vararg additionalArgs: String) : ListCommand("enumerate", emptyList(), *additionalArgs)

class Itemize(vararg additionalArgs: String) : ListCommand("itemize", emptyList(), *additionalArgs)

class Frame(
        frameTitle: String,
        vararg additionalArgs: String
) : BlockCommand("frame", listOf(frameTitle), *additionalArgs)

fun document(init: Document.() -> Unit): Document {
    val doc = Document()
    doc.init()
    return doc
}

fun resultTex(args: Array<String>) =
        document {
            documentClass("article", "12pt")

            usepackage("babel", "english", "russian")

            frame("First frame") {

            }

            enumerate {
                item {

                }

                item {

                }
            }
        }.toString()
