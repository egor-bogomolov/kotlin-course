package ru.spbau.mit

import org.junit.Test
import kotlin.test.assertEquals

class TestSource {

    @Test
    fun testDocument() {
        val doc = document {
            documentclass("article", "12pt")
            usepackage("babel", "russian", "english")
            usepackage("amsmath")
            usepackage("geometry",
                    "a4paper", "left=15mm", "right=15mm", "top=30mm", "bottom=20mm")
            +"Hello, world!"
        }
        assertEquals("""
            |\documentclass[12pt]{article}
            |\usepackage[russian,english]{babel}
            |\usepackage{amsmath}
            |\usepackage[a4paper,left=15mm,right=15mm,top=30mm,bottom=20mm]{geometry}
            |\begin{document}
            |Hello, world!
            |\end{document}
            |""".trimMargin(), doc.toString())
    }

    @Test
    fun testMath() {
        val doc = document {
            documentclass("article", "12pt")
            usepackage("babel", "russian", "english")
            usepackage("amsmath")
            usepackage("geometry",
                    "a4paper", "left=15mm", "right=15mm", "top=30mm", "bottom=20mm")
            +"Fibonacci numbers:"
            math {
                +"F_0 = 1,\\ F_1 = 1"
                +"F_n = F_{n-1} + F_{n-2}"
            }
        }
        assertEquals("""
            |\documentclass[12pt]{article}
            |\usepackage[russian,english]{babel}
            |\usepackage{amsmath}
            |\usepackage[a4paper,left=15mm,right=15mm,top=30mm,bottom=20mm]{geometry}
            |\begin{document}
            |Fibonacci numbers:
            |$$
            |F_0 = 1,\ F_1 = 1
            |F_n = F_{n-1} + F_{n-2}
            |$$
            |\end{document}
            |""".trimMargin(), doc.toString())
    }

    @Test
    fun testCustomInlineCommandInMath() {
        val doc = document {
            documentclass("article", "12pt")
            usepackage("babel", "russian", "english")
            usepackage("amsmath")
            usepackage("geometry",
                    "a4paper", "left=15mm", "right=15mm", "top=30mm", "bottom=20mm")
            math {
                customInlineTag("frac", listOf("1", "2"))
                customInlineTag("sqrt", listOf("566"))
            }
        }
        assertEquals("""
            |\documentclass[12pt]{article}
            |\usepackage[russian,english]{babel}
            |\usepackage{amsmath}
            |\usepackage[a4paper,left=15mm,right=15mm,top=30mm,bottom=20mm]{geometry}
            |\begin{document}
            |$$
            |\frac{1}{2}
            |\sqrt{566}
            |$$
            |\end{document}
            |""".trimMargin(), doc.toString())
    }

    @Test
    fun testEnumerate() {
        val doc = document {
            documentclass("article", "12pt")
            usepackage("babel", "russian", "english")
            usepackage("amsmath")
            usepackage("geometry",
                    "a4paper", "left=15mm", "right=15mm", "top=30mm", "bottom=20mm")
            enumerate {
                item {
                    +"First item"
                }
                item {
                    +"Second item"
                }
                item {
                    +"Third item"
                }
            }
        }
        assertEquals("""
            |\documentclass[12pt]{article}
            |\usepackage[russian,english]{babel}
            |\usepackage{amsmath}
            |\usepackage[a4paper,left=15mm,right=15mm,top=30mm,bottom=20mm]{geometry}
            |\begin{document}
            |\begin{enumerate}
            |\item
            |First item
            |\item
            |Second item
            |\item
            |Third item
            |\end{enumerate}
            |\end{document}
            |""".trimMargin(), doc.toString())
    }

    @Test
    fun testItemize() {
        val doc = document {
            documentclass("article", "12pt")
            usepackage("babel", "russian", "english")
            usepackage("amsmath")
            usepackage("geometry",
                    "a4paper", "left=15mm", "right=15mm", "top=30mm", "bottom=20mm")
            itemize {
                item {
                    +"First item"
                }
                item {
                    +"Second item"
                }
                item {
                    +"Third item"
                }
            }
        }
        assertEquals("""
            |\documentclass[12pt]{article}
            |\usepackage[russian,english]{babel}
            |\usepackage{amsmath}
            |\usepackage[a4paper,left=15mm,right=15mm,top=30mm,bottom=20mm]{geometry}
            |\begin{document}
            |\begin{itemize}
            |\item
            |First item
            |\item
            |Second item
            |\item
            |Third item
            |\end{itemize}
            |\end{document}
            |""".trimMargin(), doc.toString())
    }

    @Test
    fun testFrame() {
        val doc = document {
            documentclass("article", "12pt")
            usepackage("babel", "russian", "english")
            usepackage("amsmath")
            usepackage("geometry",
                    "a4paper", "left=15mm", "right=15mm", "top=30mm", "bottom=20mm")
            frame("Frame name", "arg1", "arg2") {
                +"At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum"
                +"deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati"
            }
        }
        assertEquals("""
            |\documentclass[12pt]{article}
            |\usepackage[russian,english]{babel}
            |\usepackage{amsmath}
            |\usepackage[a4paper,left=15mm,right=15mm,top=30mm,bottom=20mm]{geometry}
            |\begin{document}
            |\begin{frame}[arg1,arg2]{Frame name}
            |At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum
            |deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati
            |\end{frame}
            |\end{document}
            |""".trimMargin(), doc.toString())
    }

    @Test
    fun testCustomBlockTag() {
        val doc = document {
            documentclass("article", "12pt")
            usepackage("babel", "russian", "english")
            usepackage("amsmath")
            usepackage("geometry",
                    "a4paper", "left=15mm", "right=15mm", "top=30mm", "bottom=20mm")
            customBlockTag("pyglist", "language=kotlin") {
                +"""
                |val a = 1
                |val b = 2
                |println(a + b)
                """.trimMargin()
            }
        }
        assertEquals("""
            |\documentclass[12pt]{article}
            |\usepackage[russian,english]{babel}
            |\usepackage{amsmath}
            |\usepackage[a4paper,left=15mm,right=15mm,top=30mm,bottom=20mm]{geometry}
            |\begin{document}
            |\begin{pyglist}[language=kotlin]
            |val a = 1
            |val b = 2
            |println(a + b)
            |\end{pyglist}
            |\end{document}
            |""".trimMargin(), doc.toString())
    }

    @Test
    fun testAlignment() {
        val doc = document {
            documentclass("article", "12pt")
            usepackage("babel", "russian", "english")
            usepackage("amsmath")
            usepackage("geometry",
                    "a4paper", "left=15mm", "right=15mm", "top=30mm", "bottom=20mm")
            left {
                +"Left"
            }
            center {
                +"Center"
            }
            right {
                +"Right"
            }
        }
        assertEquals("""
            |\documentclass[12pt]{article}
            |\usepackage[russian,english]{babel}
            |\usepackage{amsmath}
            |\usepackage[a4paper,left=15mm,right=15mm,top=30mm,bottom=20mm]{geometry}
            |\begin{document}
            |\begin{flushleft}
            |Left
            |\end{flushleft}
            |\begin{center}
            |Center
            |\end{center}
            |\begin{flushright}
            |Right
            |\end{flushright}
            |\end{document}
            |""".trimMargin(), doc.toString())
    }

    @Test(expected = TexBuilderException::class)
    fun testNoClass() {
        document {
            usepackage("babel", "russian", "english")
            usepackage("amsmath")
            usepackage("geometry",
                    "a4paper", "left=15mm", "right=15mm", "top=30mm", "bottom=20mm")
        }.toString()
    }

    @Test(expected = TexBuilderException::class)
    fun testMultipleClasses() {
        document {
            documentclass("article", "12pt")
            usepackage("babel", "russian", "english")
            usepackage("amsmath")
            usepackage("geometry",
                    "a4paper", "left=15mm", "right=15mm", "top=30mm", "bottom=20mm")
            documentclass("article", "14pt")
        }
    }
}
