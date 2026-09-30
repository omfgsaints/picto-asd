package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordCardDao {

    @Query("SELECT * FROM saved_word_cards ORDER BY lastAccessedTimestamp DESC")
    fun getAllHistory(): Flow<List<WordCardEntity>>

    @Query("SELECT * FROM saved_word_cards WHERE isFavorite = 1 ORDER BY lastAccessedTimestamp DESC")
    fun getFavorites(): Flow<List<WordCardEntity>>

    @Query("SELECT * FROM saved_word_cards WHERE isCustom = 1 ORDER BY lastAccessedTimestamp DESC")
    fun getCustomWords(): Flow<List<WordCardEntity>>

    @Query("SELECT * FROM saved_word_cards WHERE LOWER(word) = LOWER(:word) LIMIT 1")
    suspend fun findByWord(word: String): WordCardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(card: WordCardEntity): Long

    @Update
    suspend fun update(card: WordCardEntity)

    @Delete
    suspend fun delete(card: WordCardEntity)

    @Query("DELETE FROM saved_word_cards WHERE LOWER(word) = LOWER(:word)")
    suspend fun deleteByWord(word: String)

    @Query("UPDATE saved_word_cards SET isFavorite = :isFav WHERE LOWER(word) = LOWER(:word)")
    suspend fun setFavorite(word: String, isFav: Boolean)

    @Query("UPDATE saved_word_cards SET usageCount = usageCount + 1, lastAccessedTimestamp = :timestamp WHERE LOWER(word) = LOWER(:word)")
    suspend fun incrementUsage(word: String, timestamp: Long = System.currentTimeMillis())
}
