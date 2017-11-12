package ru.spbau.mit

import org.junit.Test
import org.junit.After
import java.io.PrintStream
import org.junit.Before
import ru.spbau.mit.exceptions.InterpretationException
import ru.spbau.mit.exceptions.ParsingException
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class IntegrationTest {
    val pathToExamples = "src/test/java/ru/spbau/mit/exampleCode/"
    val file0 = pathToExamples + "example0.fpl"
    val file1 = pathToExamples + "example1.fpl"
    val file2 = pathToExamples + "example2.fpl"
    val file3 = pathToExamples + "example3.fpl"
    val file4 = pathToExamples + "example4.fpl"

    private val outContent = ByteArrayOutputStream()
    private val errContent = ByteArrayOutputStream()

    @Before
    fun setUpStreams() {
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))
    }

    @After
    fun cleanUpStreams() {
        System.setOut(null)
        System.setErr(null)
    }

    @Test
    fun testExampleAst0() {
        runFile(file0)
        assertEquals("0\n", outContent.toString())
    }

    @Test
    fun testExampleAst1() {
        runFile(file1)
        assertEquals("1 1\n2 2\n3 3\n4 5\n5 8\n", outContent.toString())
    }

    @Test
    fun testExampleAst2() {
        runFile(file2)
        assertEquals("42\n", outContent.toString())
    }

    @Test(expected = InterpretationException::class)
    fun testExampleAst3() {
        runFile(file3)
    }

    @Test(expected = ParsingException::class)
    fun testExampleAst4() {
        runFile(file4)
    }
}