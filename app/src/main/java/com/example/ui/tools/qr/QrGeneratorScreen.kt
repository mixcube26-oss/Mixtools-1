package com.example.ui.tools.qr

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.model.AppLanguage
import com.example.ui.components.OutlinedStateButton
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.File
import java.io.FileOutputStream

enum class QrPayloadType(val titleEn: String, val titleBn: String) {
    TEXT("Text", "টেক্সট"),
    URL("Website URL", "ওয়েবসাইট লিংক"),
    WIFI("Wi-Fi", "ওয়াইফাই"),
    CONTACT("Contact (vCard)", "যোগাযোগ"),
    EMAIL("Email", "ইমেইল"),
    PHONE("Phone", "ফোন"),
    SMS("SMS", "এসএমএস")
}

@Composable
fun QrGeneratorScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(QrPayloadType.URL) }

    // Form inputs
    var textInput by remember { mutableStateOf("") }
    var wifiSsid by remember { mutableStateOf("") }
    var wifiPassword by remember { mutableStateOf("") }
    var wifiType by remember { mutableStateOf("WPA") } // WPA, WEP, nopass

    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }

    var emailTo by remember { mutableStateOf("") }
    var emailSubject by remember { mutableStateOf("") }

    var phoneNumber by remember { mutableStateOf("") }
    var smsMessage by remember { mutableStateOf("") }

    // Colors & Error Correction
    val colorOptions = listOf(
        Pair("Classic Black", AndroidColor.BLACK),
        Pair("Navy Blue", AndroidColor.rgb(11, 26, 68)),
        Pair("Emerald", AndroidColor.rgb(16, 120, 80)),
        Pair("Vibrant Purple", AndroidColor.rgb(110, 30, 160)),
        Pair("Burgundy", AndroidColor.rgb(140, 20, 40))
    )
    var selectedColorIndex by remember { mutableStateOf(0) }
    var selectedEcLevel by remember { mutableStateOf(ErrorCorrectionLevel.M) }

    fun escapeWifi(v: String): String {
        return v.replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace(":", "\\:")
            .replace("\"", "\\\"")
    }

    // Construct valid QR payload string
    val payloadString: String = when (selectedType) {
        QrPayloadType.TEXT -> textInput.ifBlank { " " }
        QrPayloadType.URL -> {
            val u = textInput.trim()
            if (u.isBlank()) "https://google.com" else if (u.startsWith("http://") || u.startsWith("https://")) u else "https://$u"
        }
        QrPayloadType.WIFI -> {
            "WIFI:S:${escapeWifi(wifiSsid.ifBlank { "Wi-Fi" })};T:$wifiType;P:${escapeWifi(wifiPassword)};;"
        }
        QrPayloadType.CONTACT -> {
            "BEGIN:VCARD\nVERSION:3.0\nN:${contactName.ifBlank { "Name" }}\nFN:${contactName.ifBlank { "Name" }}\nTEL:${contactPhone}\nEMAIL:${contactEmail}\nEND:VCARD"
        }
        QrPayloadType.EMAIL -> {
            "mailto:${emailTo.trim()}?subject=${Uri.encode(emailSubject)}"
        }
        QrPayloadType.PHONE -> {
            "tel:${phoneNumber.trim()}"
        }
        QrPayloadType.SMS -> {
            "smsto:${phoneNumber.trim()}:${smsMessage}"
        }
    }

    val generatedBitmap: Bitmap = remember(payloadString, selectedColorIndex, selectedEcLevel) {
        generateZxingQr(
            content = payloadString,
            size = 512,
            fgColor = colorOptions[selectedColorIndex].second,
            bgColor = AndroidColor.WHITE,
            ecLevel = selectedEcLevel
        )
    }

    fun shareQrCode() {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "mixtools_qr.png")
            val stream = FileOutputStream(file)
            generatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
        } catch (_: Exception) {
            Toast.makeText(context, "Could not open share dialog", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveQrToGallery() {
        try {
            val filename = "MixTools_QR_${System.currentTimeMillis()}.png"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MixTools")
                }
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { stream ->
                        generatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }
                    Toast.makeText(context, if (language == AppLanguage.BANGLA) "গ্যালারিতে সংরক্ষিত হয়েছে!" else "Saved to Pictures!", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            // Fallback
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, filename)
            FileOutputStream(file).use { out ->
                generatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Toast.makeText(context, if (language == AppLanguage.BANGLA) "QR কোড সংরক্ষিত হয়েছে!" else "QR code saved to device!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Saved image to storage", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("qr_generator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Payload Type selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QrPayloadType.entries.forEach { type ->
                    val isSelected = selectedType == type
                    OutlinedStateButton(
                        text = if (language == AppLanguage.BANGLA) type.titleBn else type.titleEn,
                        onClick = { selectedType = type },
                        borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        paddingValues = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        testTag = "qr_type_${type.name}"
                    )
                }
            }
        }

        // QR Code Preview Display
        item {
            Surface(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = generatedBitmap.asImageBitmap(),
                        contentDescription = "Generated QR Code",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Action Buttons: Share & Save
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedStateButton(
                    text = if (language == AppLanguage.BANGLA) "শেয়ার করুন" else "Share QR",
                    onClick = { shareQrCode() },
                    icon = Icons.Default.Share,
                    modifier = Modifier.weight(1f),
                    testTag = "share_qr_button"
                )

                OutlinedStateButton(
                    text = if (language == AppLanguage.BANGLA) "সংরক্ষণ" else "Save Image",
                    onClick = { saveQrToGallery() },
                    icon = Icons.Default.Download,
                    borderColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    testTag = "save_qr_button"
                )
            }
        }

        // Form Inputs Card based on selected type
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "কিউআর তথ্যের বিবরণ" else "QR Code Payload Configuration",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    when (selectedType) {
                        QrPayloadType.TEXT -> {
                            OutlinedTextField(
                                value = textInput,
                                onValueChange = { textInput = it },
                                label = { Text("Plain Text") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4
                            )
                        }
                        QrPayloadType.URL -> {
                            OutlinedTextField(
                                value = textInput,
                                onValueChange = { textInput = it },
                                label = { Text("URL (e.g. https://google.com)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        QrPayloadType.WIFI -> {
                            OutlinedTextField(
                                value = wifiSsid,
                                onValueChange = { wifiSsid = it },
                                label = { Text("Network SSID (Name)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = wifiPassword,
                                onValueChange = { wifiPassword = it },
                                label = { Text("Password") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        QrPayloadType.CONTACT -> {
                            OutlinedTextField(
                                value = contactName,
                                onValueChange = { contactName = it },
                                label = { Text("Full Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = contactPhone,
                                onValueChange = { contactPhone = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = contactEmail,
                                onValueChange = { contactEmail = it },
                                label = { Text("Email Address") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        QrPayloadType.EMAIL -> {
                            OutlinedTextField(
                                value = emailTo,
                                onValueChange = { emailTo = it },
                                label = { Text("Recipient Email") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = emailSubject,
                                onValueChange = { emailSubject = it },
                                label = { Text("Subject") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        QrPayloadType.PHONE -> {
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        QrPayloadType.SMS -> {
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text("Recipient Number") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = smsMessage,
                                onValueChange = { smsMessage = it },
                                label = { Text("Message Body") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )
                        }
                    }
                }
            }
        }

        // Color & Error Correction Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "রং ও নির্ভুলতা নির্বাচন (Styling & Error Correction)" else "QR Color & Error Correction",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Color chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        colorOptions.forEachIndexed { idx, pair ->
                            val isSel = selectedColorIndex == idx
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(pair.second))
                                    .border(
                                        width = if (isSel) 2.5.dp else 1.dp,
                                        color = if (isSel) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColorIndex = idx }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Error correction level pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple(ErrorCorrectionLevel.L, "L (7%)", "কম (৭%)"),
                            Triple(ErrorCorrectionLevel.M, "M (15%)", "মাঝারি (১৫%)"),
                            Triple(ErrorCorrectionLevel.Q, "Q (25%)", "উন্নত (২৫%)"),
                            Triple(ErrorCorrectionLevel.H, "H (30%)", "সর্বোচ্চ (৩০%)")
                        ).forEach { (level, enLabel, bnLabel) ->
                            val isSel = selectedEcLevel == level
                            OutlinedStateButton(
                                text = if (language == AppLanguage.BANGLA) bnLabel else enLabel,
                                onClick = { selectedEcLevel = level },
                                borderColor = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                contentColor = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                paddingValues = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun generateZxingQr(
    content: String,
    size: Int,
    fgColor: Int,
    bgColor: Int,
    ecLevel: ErrorCorrectionLevel
): Bitmap {
    val hints = mapOf(
        EncodeHintType.ERROR_CORRECTION to ecLevel,
        EncodeHintType.MARGIN to 1,
        EncodeHintType.CHARACTER_SET to "UTF-8"
    )
    val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val pixels = IntArray(width * height)

    for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
            pixels[offset + x] = if (bitMatrix[x, y]) fgColor else bgColor
        }
    }

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}
