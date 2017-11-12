package ru.spbau.mit

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Test
import ru.spbau.mit.astBuilding.Ast
import ru.spbau.mit.astBuilding.AstBuilder
import ru.spbau.mit.exampleCode.ExampleAsts
import ru.spbau.mit.exceptions.ParsingException
import ru.spbau.mit.parser.FplLexer
import ru.spbau.mit.parser.FplParser
import kotlin.test.assertEquals

class ParsingTest {

    val pathToExamples = "src/test/java/ru/spbau/mit/exampleCode/"
    val file0 = pathToExamples + "example0.fpl"
    val file1 = pathToExamples + "example1.fpl"
    val file2 = pathToExamples + "example2.fpl"
    val file4 = pathToExamples + "example4.fpl"

    fun buildAst(fileName: String): Ast {
        val lexer = FplLexer(CharStreams.fromFileName(fileName))
        val parser = FplParser(CommonTokenStream(lexer))
        parser.buildParseTree = true
        return AstBuilder().create(parser.block())
    }

    @Test
    fun testExampleAst0() {
        assertEquals(ExampleAsts.ast0, buildAst(file0))
    }

    @Test
    fun testExampleAst1() {
        assertEquals(ExampleAsts.ast1, buildAst(file1))
    }

    @Test
    fun testExampleAst2() {
        assertEquals(ExampleAsts.ast2, buildAst(file2))
    }

    @Test(expected = ParsingException::class)
    fun testParsingException() {
        runFile(file4)
    }
}