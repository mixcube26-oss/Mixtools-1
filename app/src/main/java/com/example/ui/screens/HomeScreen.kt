package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.ToolRegistry
import com.example.data.db.RecentToolEntity
import com.example.model.AppLanguage
import com.example.model.CategoryId
import com.example.model.ToolCategory
import com.example.model.ToolItem
import com.example.ui.components.CategoryCard
import com.example.ui.components.HeroBanner
import com.example.ui.components.SearchBox
import com.example.ui.components.SectionHeader
import com.example.ui.components.ToolCard

@Composable
fun HomeScreen(
    language: AppLanguage,
    favoriteIds: List<String>,
    recentEntities: List<RecentToolEntity>,
    onToolClick: (ToolItem) -> Unit,
    onCategoryClick: (ToolCategory) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSeeAllCategoriesClick: () -> Unit,
    onSeeAllPopularClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchPlaceholder = if (language == AppLanguage.BANGLA) "টুলস খুঁজুন..." else "Search tools..."
    val heroTitle = if (language == AppLanguage.BANGLA) "মিক্সটুলসের সাথে কাজের গতি বাড়ান" else "Boost Your Productivity With MixTools"
    val heroSubtitle = if (language == AppLanguage.BANGLA) "এক অ্যাপে ৩০টিরও বেশি প্রয়োজনীয় টুল। কাজ করুন স্মার্টলি!" else "30+ useful tools in one app. Work smarter, not harder!"

    val categories = remember { ToolRegistry.categories }
    val popularTools = remember { ToolRegistry.tools.filter { it.isPopular } }
    val newTools = remember { ToolRegistry.tools.filter { it.isNew } }
    val previewCategories = remember(categories) { categories.take(6) }
    val categoryToolCounts = remember(categories) {
        categories.associate { it.id to ToolRegistry.getToolsForCategory(it.id).size }
    }

    val recentTools = remember(recentEntities) {
        recentEntities.mapNotNull { recent ->
            ToolRegistry.findToolById(recent.toolId)
        }.distinctBy { it.id }.take(5)
    }

    val favoriteTools = remember(favoriteIds) {
        favoriteIds.mapNotNull { favId ->
            ToolRegistry.findToolById(favId)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search bar
        item {
            SearchBox(
                query = "",
                onQueryChange = {},
                placeholder = searchPlaceholder,
                readOnly = true,
                onClick = onSearchClick
            )
        }

        // Hero Banner
        item {
            HeroBanner(
                title = heroTitle,
                subtitle = heroSubtitle,
                onActionClick = onSeeAllCategoriesClick
            )
        }

        // Categories section
        item {
            SectionHeader(
                title = if (language == AppLanguage.BANGLA) "ক্যাটাগরি" else "Categories",
                seeAllText = if (language == AppLanguage.BANGLA) "সব দেখুন" else "See All",
                onSeeAllClick = onSeeAllCategoriesClick
            )

            // Grid of categories (2 columns, displaying first 6 on Home, matching mockup)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (i in previewCategories.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val cat1 = previewCategories[i]
                        CategoryCard(
                            category = cat1,
                            toolCount = categoryToolCounts[cat1.id] ?: 0,
                            language = language,
                            onClick = { onCategoryClick(cat1) },
                            modifier = Modifier.weight(1f)
                        )
                        if (i + 1 < previewCategories.size) {
                            val cat2 = previewCategories[i + 1]
                            CategoryCard(
                                category = cat2,
                                toolCount = categoryToolCounts[cat2.id] ?: 0,
                                language = language,
                                onClick = { onCategoryClick(cat2) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Favourites section (if any)
        if (favoriteTools.isNotEmpty()) {
            item {
                SectionHeader(
                    title = if (language == AppLanguage.BANGLA) "পছন্দের টুলস" else "Favorite Tools"
                )
            }
            items(favoriteTools) { tool ->
                ToolCard(
                    tool = tool,
                    language = language,
                    isFavorite = true,
                    onToggleFavorite = { onToggleFavorite(tool.id) },
                    onClick = { onToolClick(tool) }
                )
            }
        }

        // Recently Used section (if any)
        if (recentTools.isNotEmpty()) {
            item {
                SectionHeader(
                    title = if (language == AppLanguage.BANGLA) "সাম্প্রতিক ব্যবহার" else "Recently Used"
                )
            }
            items(recentTools) { tool ->
                ToolCard(
                    tool = tool,
                    language = language,
                    isFavorite = favoriteIds.contains(tool.id),
                    onToggleFavorite = { onToggleFavorite(tool.id) },
                    onClick = { onToolClick(tool) }
                )
            }
        }

        // Popular Tools section
        item {
            SectionHeader(
                title = if (language == AppLanguage.BANGLA) "জনপ্রিয় টুলস" else "Popular Tools",
                seeAllText = if (language == AppLanguage.BANGLA) "সব দেখুন" else "See All",
                onSeeAllClick = onSeeAllPopularClick
            )
        }
        items(popularTools.take(4)) { tool ->
            ToolCard(
                tool = tool,
                language = language,
                isFavorite = favoriteIds.contains(tool.id),
                onToggleFavorite = { onToggleFavorite(tool.id) },
                onClick = { onToolClick(tool) }
            )
        }

        // New Tools section
        if (newTools.isNotEmpty()) {
            item {
                SectionHeader(
                    title = if (language == AppLanguage.BANGLA) "নতুন টুলস" else "New Tools"
                )
            }
            items(newTools) { tool ->
                ToolCard(
                    tool = tool,
                    language = language,
                    isFavorite = favoriteIds.contains(tool.id),
                    onToggleFavorite = { onToggleFavorite(tool.id) },
                    onClick = { onToolClick(tool) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
