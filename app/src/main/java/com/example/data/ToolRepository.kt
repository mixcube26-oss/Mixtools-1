package com.example.data

import com.example.data.db.FavoriteEntity
import com.example.data.db.MixToolsDatabase
import com.example.data.db.RecentToolEntity
import com.example.model.ToolItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ToolRepository(private val database: MixToolsDatabase) {

    fun getFavoriteToolIds(): Flow<List<String>> {
        return database.favoriteDao().getAllFavorites().map { list ->
            list.map { it.toolId }
        }
    }

    fun isFavorite(toolId: String): Flow<Boolean> {
        return database.favoriteDao().isFavorite(toolId)
    }

    suspend fun toggleFavorite(toolId: String, currentIsFav: Boolean) {
        if (currentIsFav) {
            database.favoriteDao().removeFavorite(toolId)
        } else {
            database.favoriteDao().addFavorite(FavoriteEntity(toolId = toolId))
        }
    }

    fun getRecentTools(): Flow<List<RecentToolEntity>> {
        return database.recentToolDao().getRecentTools()
    }

    suspend fun recordToolUsage(toolId: String) {
        val existing = database.recentToolDao().getRecentByToolId(toolId)
        val count = (existing?.launchCount ?: 0) + 1
        database.recentToolDao().upsertRecent(
            RecentToolEntity(
                toolId = toolId,
                lastUsedTimestamp = System.currentTimeMillis(),
                launchCount = count
            )
        )
    }

    suspend fun clearHistory() {
        database.recentToolDao().clearHistory()
    }

    fun getToolsForRecents(recentEntities: List<RecentToolEntity>): List<Pair<ToolItem, RecentToolEntity>> {
        return recentEntities.mapNotNull { entity ->
            val tool = ToolRegistry.findToolById(entity.toolId)
            if (tool != null) Pair(tool, entity) else null
        }
    }

    fun getToolsForFavorites(favoriteIds: List<String>): List<ToolItem> {
        return favoriteIds.mapNotNull { id ->
            ToolRegistry.findToolById(id)
        }
    }
}
