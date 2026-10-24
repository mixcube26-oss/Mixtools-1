package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.ToolRegistry
import com.example.model.AppLanguage
import com.example.model.ToolItem
import com.example.ui.components.EmptyStateView
import com.example.ui.components.SectionHeader
import com.example.ui.components.ToolCard

@Composable
fun FavoritesScreen(
    language: AppLanguage,
    favoriteIds: List<String>,
    onToolClick: (ToolItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onBrowseToolsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteTools = favoriteIds.mapNotNull { id ->
        ToolRegistry.findToolById(id)
    }

    val title = if (language == AppLanguage.BANGLA) "পছন্দের টুলস" else "Favorite Tools"
    val emptyTitle = if (language == AppLanguage.BANGLA) "কোনো পছন্দের টুল নেই" else "No Favorite Tools Yet"
    val emptySubtitle = if (language == AppLanguage.BANGLA) 
        "আপনার প্রয়োজনীয় টুল সহজে পেতে হার্ট আইকনে ট্যাপ করে পছন্দের তালিকায় যুক্ত করুন।" 
    else 
        "Tap the heart icon on any tool card to add it to your quick-access favorites list."
    val ctaText = if (language == AppLanguage.BANGLA) "টুলস ব্রাউজ করুন" else "Browse All Tools"

    if (favoriteTools.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.FavoriteBorder,
            title = emptyTitle,
            subtitle = emptySubtitle,
            actionButtonText = ctaText,
            onActionClick = onBrowseToolsClick,
            modifier = modifier.fillMaxSize()
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("favorites_screen_lazy_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader(title = "$title (${favoriteTools.size})")
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

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
