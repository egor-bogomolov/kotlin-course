package ru.spbau.mit.exceptions

/**
 * Thrown if antlr is unable to parse file.
 */
data class ParsingException(private val error: String) : Exception(error)
