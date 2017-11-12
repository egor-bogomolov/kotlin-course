package ru.spbau.mit

import ru.spbau.mit.exceptions.InterpretationException
import ru.spbau.mit.parser.FplParser

class Function(
        private val name: String,
        private val parameterNames: List<String>,
        private val initializationScope: Scope,
        private val block: FplParser.BlockWithBracesContext
) {
    fun invoke(arguments: List<Int>): Int {
        if (arguments.size != parameterNames.size) {
            throw InterpretationException("$name was called with invalid number of arguments")
        }
        val runScope = Scope(initializationScope)
        parameterNames.forEachIndexed { index, name ->
            runScope.initializeVariable(name)
            runScope.setVariableValue(name, arguments[index])
        }
        return FplAstVisitor(runScope).visit(block) ?: 0
    }
}