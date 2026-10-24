package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.ToolRegistry
import com.example.model.AppLanguage
import com.example.model.ToolCategory
import com.example.ui.components.CategoryCard
import com.example.ui.components.SectionHeader

@Composable
fun CategoriesScreen(
    language: AppLanguage,
    onCategoryClick: (ToolCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = ToolRegistry.categories
    val title = if (language == AppLanguage.BANGLA) "সকল ক্যাটাগরি" else "All Categories"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("categories_screen_lazy_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(title = title)
        }

        // 2-column grid inside LazyColumn
        val rows = categories.chunked(2)
        items(rows.size) { rowIndex ->
            val pair = rows[rowIndex]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryCard(
                    category = pair[0],
                    toolCount = ToolRegistry.getToolsForCategory(pair[0].id).size,
                    language = language,
                    onClick = { onCategoryClick(pair[0]) },
                    modifier = Modifier.weight(1f)
                )
                if (pair.size > 1) {
                    CategoryCard(
                        category = pair[1],
                        toolCount = ToolRegistry.getToolsForCategory(pair[1].id).size,
                        language = language,
                        onClick = { onCategoryClick(pair[1]) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
