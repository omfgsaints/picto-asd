package com.example.data.repository

import com.example.data.local.WordCardDao
import com.example.data.local.WordCardEntity
import com.example.data.model.WordCard
import com.example.data.network.OnlineImageFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WordRepository(
    private val dao: WordCardDao,
    private val imageFetcher: OnlineImageFetcher
) {

    val favorites: Flow<List<WordCard>> = dao.getFavorites().map { list ->
        list.map { enrichWithLocalOfflineImage(it.toWordCard()) }
    }

    val history: Flow<List<WordCard>> = dao.getAllHistory().map { list ->
        list.map { enrichWithLocalOfflineImage(it.toWordCard()) }
    }

    val customWords: Flow<List<WordCard>> = dao.getCustomWords().map { list ->
        list.map { enrichWithLocalOfflineImage(it.toWordCard()) }
    }

    suspend fun recordWordUsage(card: WordCard) = withContext(Dispatchers.IO) {
        val existing = dao.findByWord(card.word)
        if (existing != null) {
            dao.incrementUsage(card.word)
        } else {
            dao.insertOrUpdate(WordCardEntity.fromWordCard(card))
        }
    }

    suspend fun toggleFavorite(card: WordCard): Boolean = withContext(Dispatchers.IO) {
        val existing = dao.findByWord(card.word)
        val newFav = if (existing != null) !existing.isFavorite else true
        if (existing != null) {
            dao.setFavorite(card.word, newFav)
        } else {
            dao.insertOrUpdate(WordCardEntity.fromWordCard(card, isFav = true))
        }
        newFav
    }

    suspend fun saveCustomWord(card: WordCard) = withContext(Dispatchers.IO) {
        val entity = WordCardEntity.fromWordCard(card.copy(isCustom = true))
        dao.insertOrUpdate(entity)
    }

    suspend fun deleteWord(word: String) = withContext(Dispatchers.IO) {
        dao.deleteByWord(word)
    }

    suspend fun isWordFavorited(word: String): Boolean = withContext(Dispatchers.IO) {
        dao.findByWord(word)?.isFavorite == true
    }

    /**
     * Checks if this card has an offline image downloaded on disk,
     * and attaches it so it works seamlessly offline.
     */
    fun enrichWithLocalOfflineImage(card: WordCard): WordCard {
        val localFile = imageFetcher.getLocalOfflineImage(card.word)
        return if (localFile != null) {
            card.copy(
                customImagePath = localFile.absolutePath,
                isDownloadedForOffline = true
            )
        } else {
            card
        }
    }

    /**
     * Fetches an online picture for the word and caches it to disk
     * so it is permanently available offline.
     */
    suspend fun fetchAndSaveOnlineImage(card: WordCard): WordCard? = withContext(Dispatchers.IO) {
        val result = imageFetcher.fetchAndSaveForOffline(card.word) ?: return@withContext null

        val updatedCard = card.copy(
            customImagePath = result.localFilePath,
            imageUrl = result.webImageUrl,
            isDownloadedForOffline = true,
            description = result.description ?: card.description
        )

        // Save in Room database
        val existing = dao.findByWord(card.word)
        val isFav = existing?.isFavorite ?: card.isFavorite
        dao.insertOrUpdate(WordCardEntity.fromWordCard(updatedCard, isFav = isFav))

        updatedCard
    }
}
