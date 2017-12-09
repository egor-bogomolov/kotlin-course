package ru.spbau.mit.exceptions

fun <T> T?.mustBeNotNull(): T = this ?: throw InterpretationException("Unexpected null.")

/**
 * Thrown if FPL code doesn't work correctly.
 */
data class InterpretationException(private val error: String) : Exception(error)
