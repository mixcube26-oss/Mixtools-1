package com.example.ui.tools.image

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AppLanguage
import com.example.ui.components.OutlinedStateButton

@Composable
fun ImageToolsScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageWidth by remember { mutableIntStateOf(0) }
    var imageHeight by remember { mutableIntStateOf(0) }
    var rotationDegrees by remember { mutableIntStateOf(0) }
    var dominantColorHexes by remember { mutableStateOf<List<Long>>(emptyList()) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()

                imageWidth = options.outWidth
                imageHeight = options.outHeight

                // Sample bitmap for color extraction
                val sampleStream = context.contentResolver.openInputStream(uri)
                val sampleOptions = BitmapFactory.Options().apply { inSampleSize = 8 }
                val sampledBitmap = BitmapFactory.decodeStream(sampleStream, null, sampleOptions)
                sampleStream?.close()

                if (sampledBitmap != null) {
                    val colors = mutableListOf<Long>()
                    val stepX = (sampledBitmap.width / 5).coerceAtLeast(1)
                    val stepY = (sampledBitmap.height / 5).coerceAtLeast(1)
                    for (x in 0 until sampledBitmap.width step stepX) {
                        for (y in 0 until sampledBitmap.height step stepY) {
                            val pixel = sampledBitmap.getPixel(x, y)
                            colors.add(pixel.toLong() and 0xFFFFFFFF)
                        }
                    }
                    dominantColorHexes = colors.distinct().take(6)
                }
            } catch (_: Exception) {}
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("image_tools_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Picker Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedImageUri != null) {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(rotationDegrees.toFloat()),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedStateButton(
                                text = "Rotate 90°",
                                onClick = { rotationDegrees = (rotationDegrees + 90) % 360 },
                                icon = Icons.Default.RotateRight,
                                testTag = "rotate_image_button"
                            )

                            OutlinedStateButton(
                                text = if (language == AppLanguage.BANGLA) "অন্য ছবি বাছুন" else "Change Image",
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                testTag = "change_image_button"
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (language == AppLanguage.BANGLA) "গ্যালারি থেকে ছবি নির্বাচন করুন" else "Pick an Image from Device",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "জিরো-পারমিশন সুরক্ষিত ফটো পিকার ব্যবহার করে ছবির সাইজ ও কালার প্যালেট বিশ্লেষণ করুন।"
                            else
                                "Zero-permission Android Photo Picker to inspect resolution, aspect ratio & color palettes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedStateButton(
                            text = if (language == AppLanguage.BANGLA) "ছবি নির্বাচন" else "Select Photo",
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                            },
                            testTag = "select_photo_button"
                        )
                    }
                }
            }
        }

        // Image Metadata Details
        if (selectedImageUri != null && imageWidth > 0) {
            val gcd = findGcd(imageWidth, imageHeight)
            val aspectX = imageWidth / gcd
            val aspectY = imageHeight / gcd

            val specs = listOf(
                (if (language == AppLanguage.BANGLA) "রেজোলিউশন (পিক্সেল)" else "Resolution") to "${imageWidth} × ${imageHeight} px",
                (if (language == AppLanguage.BANGLA) "অ্যাসপেক্ট রেশিও" else "Aspect Ratio") to "$aspectX:$aspectY",
                (if (language == AppLanguage.BANGLA) "মেগাপিক্সেল" else "Megapixels") to String.format("%.2f MP", (imageWidth * imageHeight) / 1000000.0),
                (if (language == AppLanguage.BANGLA) "ঘূর্ণন কোণ" else "Current Rotation") to "$rotationDegrees°"
            )

            items(specs) { (label, value) ->
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            // Extracted Color Palette
            if (dominantColorHexes.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "ছবির প্রধান কালার প্যালেট" else "Dominant Color Palette",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                dominantColorHexes.forEach { colorLong ->
                                    val hexString = "#" + (colorLong and 0xFFFFFF).toString(16).padStart(6, '0').uppercase()
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(colorLong))
                                                .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(hexString, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun findGcd(a: Int, b: Int): Int {
    var x = a
    var y = b
    while (y != 0) {
        val t = y
        y = x % y
        x = t
    }
    return if (x == 0) 1 else x
}
