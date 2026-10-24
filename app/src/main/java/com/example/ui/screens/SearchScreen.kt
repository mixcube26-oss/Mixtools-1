package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ToolRegistry
import com.example.model.AppLanguage
import com.example.model.CategoryId
import com.example.model.ToolItem
import com.example.ui.components.EmptyStateView
import com.example.ui.components.SearchBox
import com.example.ui.components.SectionHeader
import com.example.ui.components.ToolCard

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedCategoryFilter: CategoryId?,
    onCategoryFilterChange: (CategoryId?) -> Unit,
    searchResults: List<ToolItem>,
    language: AppLanguage,
    favoriteIds: List<String>,
    onToolClick: (ToolItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchPlaceholder = if (language == AppLanguage.BANGLA) "নাম, ক্যাটাগরি বা বিষয় দিয়ে খুঁজুন..." else "Search by name, category or keywords..."
    val allLabel = if (language == AppLanguage.BANGLA) "সব" else "All"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 8.dp)
            .testTag("search_screen_container")
    ) {
        // Search bar
        SearchBox(
            query = query,
            onQueryChange = onQueryChange,
            placeholder = searchPlaceholder,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategoryFilter == null,
                onClick = { onCategoryFilterChange(null) },
                label = { Text(allLabel) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("filter_chip_all")
            )

            ToolRegistry.categories.forEach { category ->
                val label = if (language == AppLanguage.BANGLA) category.titleBn else category.titleEn
                FilterChip(
                    selected = selectedCategoryFilter == category.id,
                    onClick = {
                        onCategoryFilterChange(
                            if (selectedCategoryFilter == category.id) null else category.id
                        )
                    },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("filter_chip_${category.id.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Results
        if (searchResults.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.SearchOff,
                title = if (language == AppLanguage.BANGLA) "কোনো টুল পাওয়া যায়নি" else "No Tools Found",
                subtitle = if (language == AppLanguage.BANGLA)
                    "'$query' এর জন্য কোনো টুল খুঁজে পাওয়া যায়নি। অন্য শব্দ দিয়ে চেষ্টা করুন।"
                else
                    "No tools matched '$query'. Try searching in English or Bangla (e.g. 'text', 'ছবি', 'calculator', 'কিউআর').",
                actionButtonText = if (language == AppLanguage.BANGLA) "অনুসন্ধান মুছুন" else "Clear Search",
                onActionClick = {
                    onQueryChange("")
                    onCategoryFilterChange(null)
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("search_results_lazy_column"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "ফলাফল (${searchResults.size} টি টুল)"
                        else
                            "Results (${searchResults.size} tools)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(searchResults) { tool ->
                    ToolCard(
                        tool = tool,
                        language = language,
                        isFavorite = favoriteIds.contains(tool.id),
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
}
