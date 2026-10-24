package com.example.ui.screens

import android.text.format.DateUtils
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ToolRegistry
import com.example.data.db.RecentToolEntity
import com.example.model.AppLanguage
import com.example.model.ToolItem
import com.example.ui.components.EmptyStateView
import com.example.ui.components.OutlinedStateButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.ToolCard

@Composable
fun HistoryScreen(
    language: AppLanguage,
    recentEntities: List<RecentToolEntity>,
    favoriteIds: List<String>,
    onToolClick: (ToolItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onClearHistory: () -> Unit,
    onBrowseToolsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmClearDialog by remember { mutableStateOf(false) }

    val recentToolsWithData = recentEntities.mapNotNull { entity ->
        val tool = ToolRegistry.findToolById(entity.toolId)
        if (tool != null) Pair(tool, entity) else null
    }

    val title = if (language == AppLanguage.BANGLA) "ব্যবহারের ইতিহাস" else "Usage History"
    val emptyTitle = if (language == AppLanguage.BANGLA) "কোনো ব্যবহারের ইতিহাস নেই" else "No Recent Tools Yet"
    val emptySubtitle = if (language == AppLanguage.BANGLA)
        "আপনি যখন কোনো টুল চালু করবেন, তা ক্রমানুসারে এখানে প্রদর্শিত হবে।"
    else
        "Tools you genuinely open will automatically appear here in chronological order."
    val ctaText = if (language == AppLanguage.BANGLA) "টুলস শুরু করুন" else "Explore Tools"

    if (showConfirmClearDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmClearDialog = false },
            title = {
                Text(
                    text = if (language == AppLanguage.BANGLA) "ইতিহাস মুছে ফেলতে চান?" else "Clear Usage History?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == AppLanguage.BANGLA)
                        "আপনার সকল সাম্প্রতিক টুলের ইতিহাস স্থায়ীভাবে মুছে ফেলা হবে।"
                    else
                        "This will permanently clear your chronological recent tool launch history."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmClearDialog = false
                        onClearHistory()
                    },
                    modifier = Modifier.testTag("confirm_clear_history_button")
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "মুছুন" else "Clear",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClearDialog = false }) {
                    Text(text = if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel")
                }
            }
        )
    }

    if (recentToolsWithData.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.History,
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
                .testTag("history_screen_lazy_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$title (${recentToolsWithData.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedStateButton(
                        text = if (language == AppLanguage.BANGLA) "মুছে ফেলুন" else "Clear All",
                        onClick = { showConfirmClearDialog = true },
                        icon = Icons.Default.DeleteSweep,
                        borderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.error,
                        paddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        testTag = "clear_history_button"
                    )
                }
            }

            items(recentToolsWithData) { (tool, entity) ->
                val timeSpan = DateUtils.getRelativeTimeSpanString(
                    entity.lastUsedTimestamp,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                )
                val launchCountLabel = if (language == AppLanguage.BANGLA) 
                    "ব্যবহার: ${entity.launchCount} বার • $timeSpan" 
                else 
                    "Launched ${entity.launchCount}x • $timeSpan"

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = launchCountLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)
                    )
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
}
