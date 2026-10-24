package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val toolId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_tools")
data class RecentToolEntity(
    @PrimaryKey
    val toolId: String,
    val lastUsedTimestamp: Long = System.currentTimeMillis(),
    val launchCount: Int = 1
)

@Entity(tableName = "draft_notes")
data class DraftNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val fontStyle: String,
    val fontSize: Int,
    val textColor: Long,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "exchange_rates")
data class ExchangeRateCacheEntity(
    @PrimaryKey
    val currencyCode: String,
    val rateAgainstUsd: Double,
    val lastUpdatedMillis: Long
)

@Entity(tableName = "currency_conversions")
data class CurrencyConversionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromCurrency: String,
    val toCurrency: String,
    val fromAmount: Double,
    val toAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "writer_documents")
data class WriterDocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val contentHtml: String,
    val plainText: String = "",
    val pageFormat: String = "A4",
    val isLandscape: Boolean = false,
    val marginTopMm: Float = 20f,
    val marginBottomMm: Float = 20f,
    val marginLeftMm: Float = 20f,
    val marginRightMm: Float = 20f,
    val paperColorHex: String = "#FFFFFF",
    val wordCount: Int = 0,
    val charCount: Int = 0,
    val pageCount: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

