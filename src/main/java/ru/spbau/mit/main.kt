package ru.spbau.mit

fun main(args: Array<String>) {
    document {
        documentclass("article", "12pt")
        usepackage("babel", "russian", "english")
        usepackage("amsmath")
        usepackage("geometry",
                "a4paper", "left=15mm", "right=15mm", "top=30mm", "bottom=20mm")
        +"Hello, world!"
    }.toOutputStream(System.out)
}
