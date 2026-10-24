package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteEntity::class,
        RecentToolEntity::class,
        DraftNoteEntity::class,
        ExchangeRateCacheEntity::class,
        CurrencyConversionEntity::class,
        WriterDocumentEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MixToolsDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentToolDao(): RecentToolDao
    abstract fun draftNoteDao(): DraftNoteDao
    abstract fun exchangeRateDao(): ExchangeRateDao
    abstract fun currencyConversionDao(): CurrencyConversionDao
    abstract fun writerDocumentDao(): WriterDocumentDao

    companion object {
        @Volatile
        private var INSTANCE: MixToolsDatabase? = null

        fun getInstance(context: Context): MixToolsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MixToolsDatabase::class.java,
                    "mixtools_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
