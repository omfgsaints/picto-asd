package com.example.data.model

data class LetterBankTile(
    val id: Int,
    val char: Char,
    val isUsed: Boolean = false
)

data class SpellingWordItem(
    val id: String,
    val word: String,
    val label: String,
    val category: WordCategory,
    val card: WordCard,
    val level: Int,
    val funFact: String
)
