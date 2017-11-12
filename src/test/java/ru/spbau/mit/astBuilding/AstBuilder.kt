package ru.spbau.mit.astBuilding

import org.antlr.v4.runtime.ParserRuleContext
import ru.spbau.mit.parser.FplBaseVisitor
import ru.spbau.mit.parser.FplParser
import ru.spbau.mit.astBuilding.Ast.*

class AstBuilder : FplBaseVisitor<Node>() {

    fun create(ctx: ParserRuleContext): Ast {
        return Ast(visit(ctx))
    }

    override fun visitBlock(ctx: FplParser.BlockContext?): Node {
        return Block(ctx!!.statement().map { visit(it) as Statement })
    }

    override fun visitBlockWithBraces(ctx: FplParser.BlockWithBracesContext?): Ast.Node {
        return BlockWithBraces(ctx!!.block().statement().map { visit(it) as Statement })
    }

    override fun visitFunction(ctx: FplParser.FunctionContext?): Ast.Node {
        return Function(
                Identifier(ctx!!.Identifier().text),
                ctx.parameterNames().Identifier().map { Identifier(it.text) },
                visit(ctx.blockWithBraces()) as BlockWithBraces
        )
    }

    override fun visitNamedFunctionCall(ctx: FplParser.NamedFunctionCallContext?): Ast.Node {
        return NamedFunctionCall(
                Identifier(ctx!!.Identifier().text),
                ctx.arguments().expression().map { visit(it) as Expression }
        )
    }

    override fun visitPrintLnFunctionCall(ctx: FplParser.PrintLnFunctionCallContext?): Ast.Node {
        return PrintlnFunctionCall(
                ctx!!.arguments().expression().map { visit(it) as Expression }
        )
    }

    override fun visitVariable(ctx: FplParser.VariableContext?): Ast.Node {
        return Variable(
                Identifier(ctx!!.Identifier().text),
                visit(ctx.expression()) as Expression
        )
    }

    override fun visitWhileStatement(ctx: FplParser.WhileStatementContext?): Ast.Node {
        return WhileStatement(
                visit(ctx!!.expression()) as Expression,
                visit(ctx.blockWithBraces()) as BlockWithBraces
        )
    }

    override fun visitIfStatement(ctx: FplParser.IfStatementContext?): Ast.Node {
        return IfStatement(
                visit(ctx!!.expression()) as Expression,
                visit(ctx.blockWithBraces(0)) as BlockWithBraces,
                if (ctx.blockWithBraces().size >= 2) visit(ctx.blockWithBraces(1)) as BlockWithBraces else null
        )
    }

    override fun visitAssignment(ctx: FplParser.AssignmentContext?): Ast.Node {
        return Assignment(
                Identifier(ctx!!.Identifier().text),
                visit(ctx.expression()) as Expression
        )
    }

    override fun visitReturnStatement(ctx: FplParser.ReturnStatementContext?): Ast.Node {
        return ReturnStatement(visit(ctx!!.expression()) as Expression)
    }

    override fun visitBinaryExpression(ctx: FplParser.BinaryExpressionContext?): Ast.Node {
        return BinaryExpression(
                visit(ctx!!.expression(0)) as Expression,
                ctx.op.text,
                if (ctx.expression().size >= 2) visit(ctx.expression(1)) as Expression else null
        )
    }

    override fun visitUnaryMinusExpression(ctx: FplParser.UnaryMinusExpressionContext?): Ast.Node {
        return UnaryMinusExpression(visit(ctx!!.expression()) as Expression)
    }

    override fun visitIdentifierExpression(ctx: FplParser.IdentifierExpressionContext?): Ast.Node {
        return Identifier(ctx!!.Identifier().text)
    }

    override fun visitLiteralExpression(ctx: FplParser.LiteralExpressionContext?): Ast.Node {
        return Literal(ctx!!.Literal().text.toInt())
    }

}
