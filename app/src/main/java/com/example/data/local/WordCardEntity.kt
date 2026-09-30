package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.WordCard
import com.example.data.model.WordCategory

@Entity(tableName = "saved_word_cards")
data class WordCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val label: String,
    val categoryName: String,
    val emoji: String,
    val syllables: String,
    val description: String,
    val exampleSentence: String,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val customImagePath: String? = null,
    val imageUrl: String? = null,
    val isDownloadedForOffline: Boolean = false,
    val usageCount: Int = 1,
    val lastAccessedTimestamp: Long = System.currentTimeMillis()
) {
    fun toWordCard(): WordCard {
        val cat = try {
            WordCategory.valueOf(categoryName)
        } catch (_: Exception) {
            WordCategory.GENERAL
        }
        return WordCard(
            word = word,
            label = label,
            category = cat,
            emoji = emoji,
            syllables = syllables,
            description = description,
            exampleSentence = exampleSentence,
            isCustom = isCustom,
            isFavorite = isFavorite,
            customImagePath = customImagePath,
            imageUrl = imageUrl,
            isDownloadedForOffline = isDownloadedForOffline
        )
    }

    companion object {
        fun fromWordCard(card: WordCard, isFav: Boolean = card.isFavorite): WordCardEntity {
            return WordCardEntity(
                word = card.word,
                label = card.label,
                categoryName = card.category.name,
                emoji = card.emoji,
                syllables = card.syllables,
                description = card.description,
                exampleSentence = card.exampleSentence,
                isFavorite = isFav,
                isCustom = card.isCustom,
                customImagePath = card.customImagePath,
                imageUrl = card.imageUrl,
                isDownloadedForOffline = card.isDownloadedForOffline,
                usageCount = 1,
                lastAccessedTimestamp = System.currentTimeMillis()
            )
        }
    }
}
