package ru.spbau.mit.exceptions

/**
 * Thrown if FPL code doesn't work correctly.
 */
data class InterpretationException(private val error: String) : Exception(error)
