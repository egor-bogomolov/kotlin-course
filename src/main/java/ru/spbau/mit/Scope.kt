package ru.spbau.mit

class Scope(private val parentScope: Scope? = null) {
    private val functions = HashMap<String, Function>()
    private val variables = HashMap<String, Int>()

    fun getVariableValue(name: String): Int
            = variables[name]
            ?: parentScope?.getVariableValue(name)
            ?: throw Exception("Variable $name isn't defined")

    fun initializeVariable(name: String) {
        if (name in variables) throw Exception("Variable $name is already defined in the scope")
        variables[name] = 0
    }

    fun setVariableValue(name: String, value: Int) {
        if (name !in variables) {
            parentScope?.setVariableValue(name, value) ?: throw Exception("Variable $name isn't defined")
        } else {
            variables[name] = value
        }
    }

    fun identifyFunction(name: String, function: Function) {
        if (name in functions) throw Exception("Function $name is already defined in the scope")
        functions[name] = function
    }

    fun getFunction(name: String): Function
            = functions[name]
            ?: parentScope?.getFunction(name)
            ?: throw Exception("Function $name isn't defined")

}