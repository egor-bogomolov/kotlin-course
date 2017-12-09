package ru.spbau.mit

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.spbau.mit.exceptions.InterpretationException
import ru.spbau.mit.exceptions.ParsingException
import ru.spbau.mit.parser.FplLexer
import ru.spbau.mit.parser.FplParser

fun runFile(fileName: String) {
    val lexer = FplLexer(CharStreams.fromFileName(fileName))
    val parser = FplParser(CommonTokenStream(lexer))
    parser.buildParseTree = true
    val block = parser.block()
    if (parser.numberOfSyntaxErrors > 0) {
        throw ParsingException("Syntax errors were met, unable to parse file.")
    }
    FplAstVisitor().visit(block)
}

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Invalid number of arguments.")
    }
    try {
        runFile(args[0])
    } catch (e: InterpretationException) {
        System.err.println("An error was met during interpretation:")
        System.err.println(e.message)
    } catch (e: ParsingException) {
        System.err.println(e.message)
    }
}
