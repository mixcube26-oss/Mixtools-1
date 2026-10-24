package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE toolId = :toolId)")
    fun isFavorite(toolId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE toolId = :toolId")
    suspend fun removeFavorite(toolId: String)
}

@Dao
interface RecentToolDao {
    @Query("SELECT * FROM recent_tools ORDER BY lastUsedTimestamp DESC LIMIT 30")
    fun getRecentTools(): Flow<List<RecentToolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecent(recent: RecentToolEntity)

    @Query("SELECT * FROM recent_tools WHERE toolId = :toolId LIMIT 1")
    suspend fun getRecentByToolId(toolId: String): RecentToolEntity?

    @Query("DELETE FROM recent_tools")
    suspend fun clearHistory()
}

@Dao
interface DraftNoteDao {
    @Query("SELECT * FROM draft_notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<DraftNoteEntity>>

    @Query("SELECT * FROM draft_notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Long): DraftNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(note: DraftNoteEntity): Long

    @Query("DELETE FROM draft_notes WHERE id = :id")
    suspend fun deleteNote(id: Long)
}

@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates")
    suspend fun getAllCachedRates(): List<ExchangeRateCacheEntity>

    @Query("SELECT * FROM exchange_rates WHERE currencyCode = :code LIMIT 1")
    suspend fun getRate(code: String): ExchangeRateCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<ExchangeRateCacheEntity>)
}

@Dao
interface CurrencyConversionDao {
    @Query("SELECT * FROM currency_conversions ORDER BY timestamp DESC LIMIT 20")
    fun getRecentConversions(): Flow<List<CurrencyConversionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversion(conversion: CurrencyConversionEntity): Long

    @Query("DELETE FROM currency_conversions")
    suspend fun clearConversions()
}

@Dao
interface WriterDocumentDao {
    @Query("SELECT * FROM writer_documents ORDER BY updatedAt DESC")
    fun getAllDocuments(): Flow<List<WriterDocumentEntity>>

    @Query("SELECT * FROM writer_documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentById(id: Long): WriterDocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDocument(doc: WriterDocumentEntity): Long

    @Query("DELETE FROM writer_documents WHERE id = :id")
    suspend fun deleteDocument(id: Long)

    @Query("UPDATE writer_documents SET title = :newTitle, updatedAt = :updatedAt WHERE id = :id")
    suspend fun renameDocument(id: Long, newTitle: String, updatedAt: Long = System.currentTimeMillis())
}

