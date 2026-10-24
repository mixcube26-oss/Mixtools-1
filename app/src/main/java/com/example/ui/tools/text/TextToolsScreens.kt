package com.example.ui.tools.text

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

// ---------------- CASE CONVERTER ----------------
@Composable
fun CaseConverterScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("MixTools All-in-One Utility Toolkit") }

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, if (language == AppLanguage.BANGLA) "কপি করা হয়েছে!" else "Copied $label!", Toast.LENGTH_SHORT).show()
    }

    val base64Encoded = runCatching {
        android.util.Base64.encodeToString(inputText.toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)
    }.getOrDefault("")

    val base64Decoded = runCatching {
        String(android.util.Base64.decode(inputText, android.util.Base64.DEFAULT), Charsets.UTF_8)
    }.getOrDefault("(Invalid Base64)")

    val cases = listOf(
        "UPPERCASE" to inputText.uppercase(),
        "lowercase" to inputText.lowercase(),
        "Title Case" to inputText.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
        "Sentence case" to inputText.lowercase().replaceFirstChar { it.uppercase() },
        "Remove Extra Spaces" to inputText.trim().replace("\\s+".toRegex(), " "),
        "Trim Leading & Trailing" to inputText.trim(),
        "Reverse Characters" to inputText.reversed(),
        "Reverse Words" to inputText.split("\\s+".toRegex()).reversed().joinToString(" "),
        "camelCase" to inputText.split("\\s+".toRegex()).mapIndexed { idx, s ->
            if (idx == 0) s.lowercase() else s.lowercase().replaceFirstChar { it.uppercase() }
        }.joinToString(""),
        "snake_case" to inputText.trim().replace("\\s+".toRegex(), "_").lowercase(),
        "kebab-case" to inputText.trim().replace("\\s+".toRegex(), "-").lowercase(),
        "Base64 Encode" to base64Encoded,
        "Base64 Decode" to base64Decoded
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("case_converter_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text(if (language == AppLanguage.BANGLA) "মূল টেক্সট প্রবেশ করান" else "Enter Source Text") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("case_input_field"),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedStateButton(
                        text = if (language == AppLanguage.BANGLA) "মুছুন" else "Clear",
                        onClick = { inputText = "" },
                        paddingValues = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        testTag = "clear_case_input"
                    )
                }
            }
        }

        items(cases) { (name, result) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.ifEmpty { "..." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { inputText = result },
                            modifier = Modifier.testTag("use_case_$name")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChangeCircle,
                                contentDescription = "Use As Input",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = { copyToClipboard(result, name) },
                            modifier = Modifier.testTag("copy_case_$name")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------- REMOVE EXTRA SPACES ----------------
@Composable
fun CleanSpacesScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("MixTools    makes    productivity    simple.\n\n\n  Clean   extra   spaces  easily!  ") }
    var cleanedText by remember { mutableStateOf("") }

    fun processClean(removeBlankLines: Boolean, trimLines: Boolean) {
        var processed = inputText.replace("[ \\t]+".toRegex(), " ")
        if (trimLines) {
            processed = processed.lines().joinToString("\n") { it.trim() }
        }
        if (removeBlankLines) {
            processed = processed.lines().filter { it.isNotBlank() }.joinToString("\n")
        }
        cleanedText = processed.trim()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("clean_spaces_screen")
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text(if (language == AppLanguage.BANGLA) "অগোছালো টেক্সট দিন" else "Input Raw Text") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("clean_spaces_input"),
            maxLines = 8
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedStateButton(
                text = if (language == AppLanguage.BANGLA) "স্পেস সরান" else "Clean Spaces",
                onClick = { processClean(removeBlankLines = false, trimLines = true) },
                modifier = Modifier.weight(1f),
                testTag = "clean_spaces_button"
            )
            OutlinedStateButton(
                text = if (language == AppLanguage.BANGLA) "সব পরিচ্ছন্ন করুন" else "Full Sanitize",
                onClick = { processClean(removeBlankLines = true, trimLines = true) },
                modifier = Modifier.weight(1f),
                testTag = "full_sanitize_button"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "পরিচ্ছন্ন ফলাফল" else "Cleaned Output",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Cleaned Text", cleanedText))
                            Toast.makeText(context, if (language == AppLanguage.BANGLA) "কপি করা হয়েছে!" else "Copied!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = cleanedText.ifEmpty { if (language == AppLanguage.BANGLA) "উপরে বাটনে চাপুন..." else "Click clean button above..." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ---------------- WORD COUNTER ----------------
@Composable
fun WordCounterScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("MixTools provides comprehensive utilities for mobile users. Write texts, calculate numbers, convert units, monitor system memory, and export documents.") }

    val charCount = text.length
    val charNoSpaces = text.replace("\\s+".toRegex(), "").length
    val words = if (text.isBlank()) emptyList() else text.trim().split("\\s+".toRegex())
    val wordCount = words.size
    val sentences = if (text.isBlank()) 0 else text.split("[.!?]+".toRegex()).filter { it.isNotBlank() }.size
    val paragraphs = if (text.isBlank()) 0 else text.split("\n+".toRegex()).filter { it.isNotBlank() }.size

    val readMinutes = (wordCount / 200.0)
    val readTimeFormatted = if (readMinutes < 1.0) "< 1 min" else "${readMinutes.toInt()} min"
    val speakMinutes = (wordCount / 130.0)
    val speakTimeFormatted = if (speakMinutes < 1.0) "< 1 min" else "${speakMinutes.toInt()} min"

    val stats = listOf(
        (if (language == AppLanguage.BANGLA) "শব্দ সংখ্যা" else "Total Words") to "$wordCount",
        (if (language == AppLanguage.BANGLA) "অক্ষর (স্পেস সহ)" else "Characters (With spaces)") to "$charCount",
        (if (language == AppLanguage.BANGLA) "অক্ষর (স্পেস ছাড়া)" else "Characters (No spaces)") to "$charNoSpaces",
        (if (language == AppLanguage.BANGLA) "বাক্য সংখ্যা" else "Sentences") to "$sentences",
        (if (language == AppLanguage.BANGLA) "অনুচ্ছেদ" else "Paragraphs") to "$paragraphs",
        (if (language == AppLanguage.BANGLA) "পড়ার সময়" else "Reading Time") to readTimeFormatted,
        (if (language == AppLanguage.BANGLA) "বলার সময়" else "Speaking Time") to speakTimeFormatted
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("word_counter_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(if (language == AppLanguage.BANGLA) "বিশ্লেষণের জন্য টেক্সট লিখুন" else "Text to Analyze") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("word_counter_input"),
                maxLines = 6
            )
        }

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
                        style = MaterialTheme.typography.bodyLarge,
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
    }
}

// ---------------- FONT CHANGER ----------------
@Composable
fun FontChangerScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("MixTools") }

    fun convertUnicode(input: String, mapOffset: (Char) -> String): String {
        val sb = StringBuilder()
        for (ch in input) {
            sb.append(mapOffset(ch))
        }
        return sb.toString()
    }

    val styledResults = listOf(
        "Bold Serif" to convertUnicode(inputText) { c ->
            when (c) {
                in 'A'..'Z' -> String(Character.toChars(0x1D400 + (c - 'A')))
                in 'a'..'z' -> String(Character.toChars(0x1D41A + (c - 'a')))
                in '0'..'9' -> String(Character.toChars(0x1D7CE + (c - '0')))
                else -> c.toString()
            }
        },
        "Italic Serif" to convertUnicode(inputText) { c ->
            when (c) {
                in 'A'..'Z' -> String(Character.toChars(0x1D434 + (c - 'A')))
                in 'a'..'z' -> String(Character.toChars(0x1D44E + (c - 'a')))
                else -> c.toString()
            }
        },
        "Script / Cursive" to convertUnicode(inputText) { c ->
            when (c) {
                in 'A'..'Z' -> String(Character.toChars(0x1D4D0 + (c - 'A')))
                in 'a'..'z' -> String(Character.toChars(0x1D4EA + (c - 'a')))
                else -> c.toString()
            }
        },
        "Double-Struck" to convertUnicode(inputText) { c ->
            when (c) {
                in 'A'..'Z' -> String(Character.toChars(0x1D538 + (c - 'A')))
                in 'a'..'z' -> String(Character.toChars(0x1D552 + (c - 'a')))
                in '0'..'9' -> String(Character.toChars(0x1D7D8 + (c - '0')))
                else -> c.toString()
            }
        },
        "Gothic / Fraktur" to convertUnicode(inputText) { c ->
            when (c) {
                in 'A'..'Z' -> String(Character.toChars(0x1D56C + (c - 'A')))
                in 'a'..'z' -> String(Character.toChars(0x1D586 + (c - 'a')))
                else -> c.toString()
            }
        },
        "Monospace" to convertUnicode(inputText) { c ->
            when (c) {
                in 'A'..'Z' -> String(Character.toChars(0x1D670 + (c - 'A')))
                in 'a'..'z' -> String(Character.toChars(0x1D68A + (c - 'a')))
                in '0'..'9' -> String(Character.toChars(0x1D7F6 + (c - '0')))
                else -> c.toString()
            }
        }
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("font_changer_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text(if (language == AppLanguage.BANGLA) "শব্দ বা বাক্য লিখুন" else "Type Any Word or Phrase") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("font_changer_input"),
                singleLine = true
            )
        }

        items(styledResults) { (styleName, styledText) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = styleName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = styledText,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText(styleName, styledText))
                            Toast.makeText(context, if (language == AppLanguage.BANGLA) "কপি করা হয়েছে!" else "Copied $styleName!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("copy_font_$styleName")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
