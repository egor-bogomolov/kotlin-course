package ru.spbau.mit.exampleCode

import ru.spbau.mit.astBuilding.Ast
import ru.spbau.mit.astBuilding.Ast.*

object ExampleAsts {
    private val a = Identifier("a")
    private val b = Identifier("b")
    private val n = Identifier("n")
    private val m = Identifier("m")
    private val i = Identifier("i")
    private val fib = Identifier("fib")
    private val foo = Identifier("foo")
    private val bar = Identifier("bar")
    private val println0 = PrintlnFunctionCall(listOf(Literal(0)))
    private val println1 = PrintlnFunctionCall(listOf(Literal(1)))

    val AST_0 = Ast(Block(listOf(
            Variable(a, Literal(10)),
            Variable(b, Literal(20)),
            IfStatement(
                    BinaryExpression(a, ">", b),
                    BlockWithBraces(listOf(println1)),
                    BlockWithBraces(listOf(println0))
            )
    )))

    val AST_1 = Ast(Block(listOf(
            Function(fib, listOf(n), BlockWithBraces(listOf(
                    IfStatement(
                            BinaryExpression(n, "<=", Literal(1)),
                            BlockWithBraces(listOf(
                                    ReturnStatement(Literal(1))
                            )),
                            null),
                    ReturnStatement(
                            BinaryExpression(
                                    NamedFunctionCall(fib, listOf(BinaryExpression(n, "-", Literal(1)))),
                                    "+",
                                    NamedFunctionCall(fib, listOf(BinaryExpression(n, "-", Literal(2))))
                            )
                    )
            ))),
            Variable(i, Literal(1)),
            WhileStatement(
                    BinaryExpression(i, "<=", Literal(5)),
                    BlockWithBraces(listOf(
                            PrintlnFunctionCall(listOf(
                                    i,
                                    NamedFunctionCall(fib, listOf(i))
                            )),
                            Assignment(i, BinaryExpression(i, "+", Literal(1)))
                    ))
            )
    )))

    val AST_2 = Ast(Block(listOf(
            Function(foo, listOf(n), BlockWithBraces(listOf(
                    Function(bar, listOf(m), BlockWithBraces(listOf(
                            ReturnStatement(BinaryExpression(m, "+", n))
                    ))),
                    ReturnStatement(NamedFunctionCall(bar, listOf(Literal(1))))
            ))),
            PrintlnFunctionCall(listOf(NamedFunctionCall(foo, listOf(Literal(41)))))
    )))
}
