package com.example.ui.tools.video

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.ui.components.OutlinedStateButton

@Composable
fun VideoToolsScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    var widthInput by remember { mutableStateOf("1920") }
    var heightInput by remember { mutableStateOf("1080") }

    var durationMinutes by remember { mutableStateOf("10") }
    var bitrateMbps by remember { mutableStateOf("8") }

    fun calculateAspectRatio(w: Int, h: Int): String {
        if (w <= 0 || h <= 0) return "16:9"
        var a = w
        var b = h
        while (b != 0) {
            val t = b
            b = a % b
            a = t
        }
        val gcd = if (a == 0) 1 else a
        return "${w / gcd}:${h / gcd}"
    }

    val currentRatio = calculateAspectRatio(
        widthInput.toIntOrNull() ?: 1920,
        heightInput.toIntOrNull() ?: 1080
    )

    val durationSec = (durationMinutes.toDoubleOrNull() ?: 0.0) * 60.0
    val bitrate = bitrateMbps.toDoubleOrNull() ?: 0.0
    // File size in MB = (bitrate Mbps * duration in sec) / 8
    val estimatedSizeMB = (bitrate * durationSec) / 8.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("video_tools_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Aspect Ratio Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ভিডিও অ্যাসপেক্ট রেশিও ক্যালকুলেটর" else "Video Aspect Ratio Calculator",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = widthInput,
                            onValueChange = { widthInput = it },
                            label = { Text("Width px") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = heightInput,
                            onValueChange = { heightInput = it },
                            label = { Text("Height px") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("1920x1080" to Pair("1920", "1080"), "1080x1920" to Pair("1080", "1920"), "1280x720" to Pair("1280", "720"), "3840x2160" to Pair("3840", "2160")).forEach { (label, dims) ->
                            OutlinedStateButton(
                                text = label,
                                onClick = {
                                    widthInput = dims.first
                                    heightInput = dims.second
                                },
                                paddingValues = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Aspect Ratio: $currentRatio",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // File Size & Bitrate Estimator
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ভিডিও ফাইল সাইজ ও বিটরেট হিসাব" else "Video Bitrate & File Size Estimator",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = durationMinutes,
                            onValueChange = { durationMinutes = it },
                            label = { Text("Duration (min)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = bitrateMbps,
                            onValueChange = { bitrateMbps = it },
                            label = { Text("Bitrate (Mbps)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = String.format("Estimated Size: %.1f MB (%.2f GB)", estimatedSizeMB, estimatedSizeMB / 1024.0),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }
    }
}
