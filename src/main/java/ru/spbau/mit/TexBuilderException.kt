package ru.spbau.mit

data class TexBuilderException(private val error: String) : Exception(error)