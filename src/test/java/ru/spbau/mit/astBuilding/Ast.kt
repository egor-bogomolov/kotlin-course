package ru.spbau.mit.astBuilding

data class Ast(private val root: Node) {

    interface Node

    interface File : Node

    data class Block(val statements: List<Statement>) : Node

    data class BlockWithBraces(val statements: List<Statement>) : Node

    interface Statement : Node

    data class Function(val name: Identifier, val parameters: List<Identifier>, val body: BlockWithBraces) : Statement

    data class NamedFunctionCall(val name: Identifier, val args: List<Expression>) : Expression

    data class PrintlnFunctionCall(val args: List<Expression>) : Statement

    data class Variable(val name: Identifier, val value: Expression?) : Statement

    data class WhileStatement(val condition: Expression, val body: BlockWithBraces) : Statement

    data class IfStatement(
            val condition: Expression,
            val body: BlockWithBraces,
            val elseBlock: BlockWithBraces?
    ) : Statement

    data class BinaryExpression(val first: Expression, val op: String, val right: Expression?) : Expression

    data class UnaryMinusExpression(val first: Expression) : Expression

    data class Assignment(val name: Identifier, val value: Expression) : Statement

    data class ReturnStatement(val value: Expression) : Statement

    interface Expression : Statement

    data class Identifier(val name: String) : Expression

    data class Literal(val value: Int) : Expression

}