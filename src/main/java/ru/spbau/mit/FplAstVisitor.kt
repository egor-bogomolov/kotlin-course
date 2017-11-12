package ru.spbau.mit

import ru.spbau.mit.parser.FplBaseVisitor
import ru.spbau.mit.parser.FplParser

class FplAstVisitor(private var scope: Scope = Scope()) : FplBaseVisitor<Int?>() {

    override fun visitBlock(ctx: FplParser.BlockContext?): Int? {
        val upperScope = scope
        scope = Scope(scope)
        ctx!!.statement().forEach {
            val result = visit(it)
            if (result != null) {
                scope = upperScope
                return result
            }
        }
        scope = upperScope
        return null
    }

    override fun visitFunction(ctx: FplParser.FunctionContext?): Int? {
        val name = ctx!!.Identifier().text
        val parameterNames = ctx.parameterNames().Identifier().map { it.text }
        val block = ctx.blockWithBraces()
        scope.identifyFunction(name, Function(parameterNames, scope, block))
        return null
    }

    override fun visitNamedFunctionCall(ctx: FplParser.NamedFunctionCallContext?): Int {
        val name = ctx!!.Identifier().text
        val arguments = ctx.arguments().expression().map { visit(it)!! }
        return scope.getFunction(name).invoke(arguments)
    }

    override fun visitPrintLnFunctionCall(ctx: FplParser.PrintLnFunctionCallContext?): Int? {
        val arguments = ctx!!.arguments().expression().map { visit(it) }
        println(arguments.joinToString(" "))
        return null
    }

    override fun visitVariable(ctx: FplParser.VariableContext?): Int? {
        val name = ctx!!.Identifier().text
        scope.initializeVariable(name)
        if (ctx.expression() != null) scope.setVariableValue(name, visit(ctx.expression())!!)
        return null
    }

    override fun visitWhileStatement(ctx: FplParser.WhileStatementContext?): Int? {
        val condition = ctx!!.expression()
        while (visit(condition) != 0) {
            val result = visit(ctx.blockWithBraces())
            if (result != null) return result
        }
        return null
    }

    override fun visitIfStatement(ctx: FplParser.IfStatementContext?): Int? {
        val condition = ctx!!.expression()
        if (visit(condition) != 0) {
            return visit(ctx.blockWithBraces(0))
        } else if (ctx.blockWithBraces().size >= 2) {
            return visit(ctx.blockWithBraces(1))
        }
        return null
    }

    override fun visitAssignment(ctx: FplParser.AssignmentContext?): Int? {
        scope.setVariableValue(ctx!!.Identifier().text, visit(ctx.expression())!!)
        return null
    }

    override fun visitReturnStatement(ctx: FplParser.ReturnStatementContext?): Int {
        return visit(ctx!!.expression())!!
    }

    override fun visitBinaryExpression(ctx: FplParser.BinaryExpressionContext?): Int {
        val first = visit(ctx!!.expression(0))!!
        val op = ctx.op.text
        when (op) {
            "||" -> if (first != 0) return 1
            "&&" -> if (first == 0) return 0
        }
        val second = visit(ctx.expression(1))!!
        return when (op) {
            "+" -> first + second
            "-" -> first - second
            "*" -> first * second
            "/" -> first / second
            "%" -> first % second
            ">" -> if (first > second) 1 else 0
            "<" -> if (first < second) 1 else 0
            ">=" -> if (first >= second) 1 else 0
            "<=" -> if (first <= second) 1 else 0
            "==" -> if (first == second) 1 else 0
            "!=" -> if (first != second) 1 else 0
            "||" -> if (second != 0) 1 else 0
            "&&" -> if (second == 0) 0 else 1
            else -> throw Exception("Unrecognized operation $op")
        }
    }

    override fun visitUnaryMinusExpression(ctx: FplParser.UnaryMinusExpressionContext?): Int {
        return -visit(ctx!!.expression())!!
    }

    override fun visitIdentifierExpression(ctx: FplParser.IdentifierExpressionContext?): Int {
        return scope.getVariableValue(ctx!!.text)
    }

    override fun visitBracesExpression(ctx: FplParser.BracesExpressionContext?): Int {
        return visit(ctx!!.expression())!!
    }

    override fun visitLiteralExpression(ctx: FplParser.LiteralExpressionContext?): Int {
        return ctx!!.Literal().text.toInt()
    }

    override fun aggregateResult(aggregate: Int?, nextResult: Int?): Int? {
        return aggregate ?: nextResult
    }

}