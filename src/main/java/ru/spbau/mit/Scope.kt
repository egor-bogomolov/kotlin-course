package ru.spbau.mit

import ru.spbau.mit.exceptions.InterpretationException
import ru.spbau.mit.exceptions.mustBeNotNull

class Scope(private val parentScope: Scope? = null) {
    private val functions = HashMap<String, Function>()
    private val variables = HashMap<String, Int>()

    fun getVariableValue(name: String): Int
            = variables[name]
            ?: parentScope.mustBeNotNull().getVariableValue(name)

    fun initializeVariable(name: String) {
        if (name in variables) throw InterpretationException("Variable $name is already defined in the scope")
        variables[name] = 0
    }

    fun setVariableValue(name: String, value: Int) {
        if (name !in variables) {
            parentScope.mustBeNotNull().setVariableValue(name, value)
        } else {
            variables[name] = value
        }
    }

    fun identifyFunction(name: String, function: Function) {
        if (name in functions) throw InterpretationException("Function $name is already defined in the scope")
        functions[name] = function
    }

    fun getFunction(name: String): Function
            = functions[name]
            ?: parentScope.mustBeNotNull().getFunction(name)

}