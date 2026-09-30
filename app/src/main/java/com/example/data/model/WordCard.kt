package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class WordCategory(
    val displayName: String,
    val emoji: String,
    val primaryColor: Color,
    val lightBgColor: Color
) {
    FOOD("Food & Drinks", "🍎", Color(0xFFE11D48), Color(0xFFFFE4E6)),
    FEELINGS("Feelings", "😊", Color(0xFFD97706), Color(0xFFFEF3C7)),
    ACTIONS("Actions", "⚡", Color(0xFF2563EB), Color(0xFFDBEAFE)),
    ANIMALS("Animals", "🐶", Color(0xFF059669), Color(0xFFD1FAE5)),
    TOYS("Toys & Play", "🧸", Color(0xFF7C3AED), Color(0xFFEDE9FE)),
    ROUTINE("Routines & Places", "🏠", Color(0xFF0891B2), Color(0xFFCFFAFE)),
    PEOPLE("People & Family", "👨‍👩‍👦", Color(0xFFEA580C), Color(0xFFFFEDD5)),
    CLOTHING("Clothes & Body", "👕", Color(0xFF4F46E5), Color(0xFFE0E7FF)),
    COLORS_SHAPES("Colors & Shapes", "🎨", Color(0xFFDB2777), Color(0xFFFCE7F3)),
    COMMUNICATION("Talk & Help", "💬", Color(0xFF16A34A), Color(0xFFDCFCE7)),
    GENERAL("All Words", "🌟", Color(0xFF475569), Color(0xFFF1F5F9))
}

data class WordCard(
    val word: String,
    val label: String = word.replaceFirstChar { it.uppercase() },
    val category: WordCategory = WordCategory.GENERAL,
    val emoji: String = "✨",
    val syllables: String = word.uppercase(),
    val description: String = "",
    val exampleSentence: String = "",
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false,
    val customImagePath: String? = null,
    val imageUrl: String? = null,
    val isDownloadedForOffline: Boolean = false
)
