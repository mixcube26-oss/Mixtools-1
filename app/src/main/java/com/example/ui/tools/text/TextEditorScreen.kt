package com.example.ui.tools.text

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.components.OutlinedStateButton
import com.example.ui.theme.PrimaryGradient

@Composable
fun TextEditorScreen(
    language: AppLanguage,
    onSaveDraft: (title: String, content: String, fontStyle: String, fontSize: Int, textColor: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var textContent by remember { mutableStateOf("") }
    var documentTitle by remember { mutableStateOf("Untitled Document") }

    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderline by remember { mutableStateOf(false) }
    var isStrikethrough by remember { mutableStateOf(false) }

    var selectedFontName by remember { mutableStateOf("Roboto") }
    var fontDropdownExpanded by remember { mutableStateOf(false) }
    var fontSize by remember { mutableIntStateOf(16) }

    val colorPalette = listOf(
        0xFFFFFFFF, // White
        0xFF94A3B8, // Slate
        0xFFEF4444, // Red
        0xFFF97316, // Orange
        0xFFF59E0B, // Amber
        0xFF10B981, // Emerald
        0xFF06B6D4, // Cyan
        0xFF3B82F6, // Blue
        0xFFA855F7, // Purple
        0xFFEC4899  // Magenta
    )
    var selectedTextColorHex by remember { mutableStateOf(0xFFFFFFFF) }

    val fontFamilies = mapOf(
        "Roboto" to FontFamily.Default,
        "Serif" to FontFamily.Serif,
        "Sans-Serif" to FontFamily.SansSerif,
        "Monospace" to FontFamily.Monospace,
        "Cursive" to FontFamily.Cursive
    )

    val currentFontFamily = fontFamilies[selectedFontName] ?: FontFamily.Default

    val characterCount = textContent.length
    val wordCount = if (textContent.isBlank()) 0 else textContent.trim().split("\\s+".toRegex()).size

    val textDecoration = when {
        isUnderline && isStrikethrough -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
        isUnderline -> TextDecoration.Underline
        isStrikethrough -> TextDecoration.LineThrough
        else -> TextDecoration.None
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("text_editor_screen")
    ) {
        // Document Title & Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = documentTitle,
                onValueChange = { documentTitle = it },
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("doc_title_field")
            )

            Row {
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("MixTools Note", textContent))
                        Toast.makeText(context, if (language == AppLanguage.BANGLA) "কপি করা হয়েছে" else "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("copy_text_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, documentTitle)
                            putExtra(Intent.EXTRA_TEXT, textContent)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share note"))
                    },
                    modifier = Modifier.testTag("share_text_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Live stats counter bar matching mockup ("0 characters • 0 words")
        Text(
            text = if (language == AppLanguage.BANGLA)
                "$characterCount অক্ষর • $wordCount শব্দ"
            else
                "$characterCount characters • $wordCount words",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 8.dp)
        )

        // Main Text Editor Area
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (textContent.isEmpty()) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "এখানে লিখুন..." else "Start typing your text here...",
                        style = TextStyle(
                            fontFamily = currentFontFamily,
                            fontSize = fontSize.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }

                BasicTextField(
                    value = textContent,
                    onValueChange = { textContent = it },
                    textStyle = TextStyle(
                        fontFamily = currentFontFamily,
                        fontSize = fontSize.sp,
                        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = textDecoration,
                        color = Color(selectedTextColorHex)
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("text_editor_input_field")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Formatting Bar matching mockup (B, I, U, S, Bullet, Numbered)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FormattingToggleButton(
                icon = Icons.Default.FormatBold,
                isActive = isBold,
                onClick = { isBold = !isBold },
                testTag = "format_bold"
            )
            FormattingToggleButton(
                icon = Icons.Default.FormatItalic,
                isActive = isItalic,
                onClick = { isItalic = !isItalic },
                testTag = "format_italic"
            )
            FormattingToggleButton(
                icon = Icons.Default.FormatUnderlined,
                isActive = isUnderline,
                onClick = { isUnderline = !isUnderline },
                testTag = "format_underline"
            )
            FormattingToggleButton(
                icon = Icons.Default.FormatStrikethrough,
                isActive = isStrikethrough,
                onClick = { isStrikethrough = !isStrikethrough },
                testTag = "format_strike"
            )
            FormattingToggleButton(
                icon = Icons.Default.FormatListBulleted,
                isActive = false,
                onClick = {
                    textContent = if (textContent.isEmpty()) "• " else "$textContent\n• "
                },
                testTag = "format_bullet"
            )
            FormattingToggleButton(
                icon = Icons.Default.FormatListNumbered,
                isActive = false,
                onClick = {
                    val lines = textContent.lines()
                    val nextNum = lines.count { it.trim().matches("^\\d+\\..*".toRegex()) } + 1
                    textContent = if (textContent.isEmpty()) "$nextNum. " else "$textContent\n$nextNum. "
                },
                testTag = "format_numbered"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Font Style & Size Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Font Style Dropdown
            Box {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { fontDropdownExpanded = true }
                        .testTag("font_style_dropdown"),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedFontName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                DropdownMenu(
                    expanded = fontDropdownExpanded,
                    onDismissRequest = { fontDropdownExpanded = false }
                ) {
                    fontFamilies.keys.forEach { name ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                selectedFontName = name
                                fontDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Font Size Stepper (- 16 +)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                IconButton(
                    onClick = { if (fontSize > 10) fontSize -= 2 },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("font_size_decrease")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "$fontSize",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = { if (fontSize < 40) fontSize += 2 },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("font_size_increase")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Text Color Palette Chips matching mockup
        Text(
            text = if (language == AppLanguage.BANGLA) "টেক্সট কালার" else "Text Color",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colorPalette.forEach { hex ->
                val isSelected = selectedTextColorHex == hex
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(hex))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .clickable { selectedTextColorHex = hex }
                        .testTag("color_chip_${hex.toString(16)}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (hex == 0xFFFFFFFF) Color.Black else Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons: Clear & Save matching mockup
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedStateButton(
                text = if (language == AppLanguage.BANGLA) "মুছুন" else "Clear",
                onClick = { textContent = "" },
                modifier = Modifier.weight(1f),
                borderColor = MaterialTheme.colorScheme.outlineVariant,
                testTag = "clear_text_button"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryGradient)
                    .clickable {
                        onSaveDraft(
                            documentTitle,
                            textContent,
                            selectedFontName,
                            fontSize,
                            selectedTextColorHex
                        )
                        Toast.makeText(
                            context,
                            if (language == AppLanguage.BANGLA) "খসড়া সংরক্ষিত হয়েছে" else "Draft saved locally!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .padding(vertical = 12.dp)
                    .testTag("save_text_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) "সংরক্ষণ" else "Save Note",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun FormattingToggleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            width = 1.dp,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
