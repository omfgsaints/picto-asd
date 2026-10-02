package com.example.data.model

data class SentenceIdentifyItem(
    val id: String,
    val spokenSentence: String,
    val promptInstruction: String,
    val targetCardKey: String,
    val optionCardKeys: List<String>,
    val explanation: String
)
