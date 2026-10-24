package com.example.ui.tools.text

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.components.OutlinedStateButton

@Composable
fun TextCounterScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var textContent by remember {
        mutableStateOf(
            "MixTools All-in-One Utility Platform.\n" +
            "মিক্সটুলস একটি পরিপূর্ণ অ্যান্ড্রয়েড ইউটিলিটি টুলকিট। " +
            "Here you can count words, characters, sentences, and estimated reading time with high accuracy."
        )
    }

    // Unicode & Language Aware Counts
    val characterCount = textContent.length
    val charNoSpaces = textContent.replace("\\s+".toRegex(), "").length

    // Words: splits on Unicode whitespace and punctuation boundaries
    val words = if (textContent.isBlank()) emptyList() else textContent.trim().split("[\\s\\p{Punct}&&[^'-]]+".toRegex()).filter { it.isNotBlank() }
    val wordCount = words.size

    // Sentences: handles common abbreviations (Dr., Mr., etc.) and Bengali dāri (।)
    val sentenceCount = if (textContent.isBlank()) 0 else {
        val sanitized = textContent
            .replace("(?i)\\b(dr|mr|mrs|ms|prof|inc|ltd|approx|e\\.g|i\\.e)\\.".toRegex(), "$1")
            .replace("(?<=\\d)\\.(?=\\d)".toRegex(), "") // decimal numbers
        sanitized.split("[.!?।\\n]+".toRegex()).filter { it.trim().length > 1 }.size
    }

    val paragraphCount = if (textContent.isBlank()) 0 else textContent.split("\\n\\s*\\n+".toRegex()).filter { it.isNotBlank() }.size
    val lineCount = if (textContent.isEmpty()) 0 else textContent.lines().size

    // Estimated Reading (200 words per minute) & Speaking (130 words per minute)
    val readMinutes = wordCount / 200.0
    val readSeconds = (readMinutes * 60).toInt()
    val readingTimeFormatted = if (readSeconds < 60) "< 1 min (${readSeconds}s)" else "${readSeconds / 60} min ${readSeconds % 60}s"

    val speakMinutes = wordCount / 130.0
    val speakSeconds = (speakMinutes * 60).toInt()
    val speakingTimeFormatted = if (speakSeconds < 60) "< 1 min (${speakSeconds}s)" else "${speakSeconds / 60} min ${speakSeconds % 60}s"

    fun copyText() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("MixTools Text", textContent))
        Toast.makeText(context, if (language == AppLanguage.BANGLA) "টেক্সট কপি করা হয়েছে!" else "Text copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    val stats = listOf(
        (if (language == AppLanguage.BANGLA) "মোট শব্দ" else "Words") to "$wordCount",
        (if (language == AppLanguage.BANGLA) "মোট অক্ষর (স্পেস সহ)" else "Characters (With spaces)") to "$characterCount",
        (if (language == AppLanguage.BANGLA) "অক্ষর (স্পেস ছাড়া)" else "Characters (No spaces)") to "$charNoSpaces",
        (if (language == AppLanguage.BANGLA) "বাক্য সংখ্যা" else "Sentences") to "$sentenceCount",
        (if (language == AppLanguage.BANGLA) "অনুচ্ছেদ" else "Paragraphs") to "$paragraphCount",
        (if (language == AppLanguage.BANGLA) "লাইন সংখ্যা" else "Lines") to "$lineCount",
        (if (language == AppLanguage.BANGLA) "পড়ার আনুমানিক সময়" else "Estimated Reading Time") to readingTimeFormatted,
        (if (language == AppLanguage.BANGLA) "বলার আনুমানিক সময়" else "Estimated Speaking Time") to speakingTimeFormatted
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("text_counter_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Text Area Input
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = textContent,
                        onValueChange = { textContent = it },
                        label = { Text(if (language == AppLanguage.BANGLA) "টেক্সট লিখুন বা পেস্ট করুন" else "Type or paste text...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("text_counter_input_field"),
                        maxLines = 15
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedStateButton(
                            text = if (language == AppLanguage.BANGLA) "কপি" else "Copy",
                            onClick = { copyText() },
                            icon = Icons.Default.ContentCopy,
                            modifier = Modifier.weight(1f),
                            testTag = "counter_copy_btn"
                        )
                        OutlinedStateButton(
                            text = if (language == AppLanguage.BANGLA) "মুছুন" else "Clear",
                            onClick = { textContent = "" },
                            icon = Icons.Default.Clear,
                            modifier = Modifier.weight(1f),
                            testTag = "counter_clear_btn"
                        )
                    }
                }
            }
        }

        // Stats Header
        item {
            Text(
                text = if (language == AppLanguage.BANGLA) "বিশ্লেষণ ফলাফল" else "Text Statistics",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Stats Cards Grid
        items(stats) { (label, value) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Estimation note
        item {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (language == AppLanguage.BANGLA)
                        "শব্দ ও বাক্যের সংখ্যা ভাষা ও স্ট্যান্ডার্ড বিরামচিহ্ন নিয়মের ভিত্তিতে আনুমানিক গণনা।"
                    else
                        "Word and sentence counts are estimates based on standard Unicode tokenization rules.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
