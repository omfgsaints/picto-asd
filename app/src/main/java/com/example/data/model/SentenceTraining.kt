package com.example.data.model

data class SentenceTrainingLevel(
    val levelNumber: Int,
    val title: String,
    val description: String,
    val icon: String,
    val exercises: List<SentenceExercise>
)

data class SentenceExercise(
    val id: String,
    val levelNumber: Int,
    val title: String,
    val promptInstruction: String,
    val promptAudioText: String,
    val emoji: String,
    val targetWords: List<String>,
    val distractorWords: List<String>,
    val slotHints: List<String>
)
