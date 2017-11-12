package ru.spbau.mit

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.spbau.mit.parser.FplLexer
import ru.spbau.mit.parser.FplParser

fun main(args: Array<String>) {
    if (args.isEmpty()) {

    }
    val lexer = FplLexer(CharStreams.fromFileName(args[0]))
    val parser = FplParser(CommonTokenStream(lexer))
    parser.buildParseTree = true
}
