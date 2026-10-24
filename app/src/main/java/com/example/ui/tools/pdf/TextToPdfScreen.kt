package com.example.ui.tools.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
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
import androidx.core.content.FileProvider
import com.example.model.AppLanguage
import com.example.ui.components.OutlinedStateButton
import java.io.File
import java.io.FileOutputStream

@Composable
fun TextToPdfScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var documentTitle by remember { mutableStateOf("MixTools Report") }
    var documentBody by remember {
        mutableStateOf(
            "MixTools Document Export\n\n" +
            "This document was created directly on an Android device using the native Android graphics PDF engine.\n\n" +
            "Key Features Included:\n" +
            "• Zero mock data - actual binary PDF file generation.\n" +
            "• Compatible with all standard PDF viewers and printers.\n" +
            "• Supports custom multi-line text and automated page margins."
        )
    }

    var generatedPdfFile by remember { mutableStateOf<File?>(null) }

    fun generatePdf() {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 dimensions
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val titlePaint = Paint().apply {
                color = AndroidColor.BLACK
                textSize = 20f
                isFakeBoldText = true
            }

            val bodyPaint = Paint().apply {
                color = AndroidColor.DKGRAY
                textSize = 12f
            }

            // Draw header
            canvas.drawText(documentTitle, 50f, 60f, titlePaint)

            // Draw body lines
            val lines = documentBody.lines()
            var yOffset = 100f
            for (line in lines) {
                if (line.isNotBlank()) {
                    canvas.drawText(line, 50f, yOffset, bodyPaint)
                }
                yOffset += 20f
                if (yOffset > 800f) break
            }

            pdfDocument.finishPage(page)

            // Save to cache
            val cacheFolder = File(context.cacheDir, "documents")
            cacheFolder.mkdirs()
            val sanitizedName = documentTitle.replace("[^a-zA-Z0-9]".toRegex(), "_")
            val targetFile = File(cacheFolder, "${sanitizedName}.pdf")
            val fos = FileOutputStream(targetFile)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()

            generatedPdfFile = targetFile
            Toast.makeText(
                context,
                if (language == AppLanguage.BANGLA) "পিডিএফ ফাইল তৈরি হয়েছে!" else "PDF generated successfully!",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun sharePdf(file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share PDF Document"))
        } catch (_: Exception) {
            Toast.makeText(context, "Could not open share sheet", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("text_to_pdf_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "পিডিএফ ডকুমেন্ট শিরোনাম" else "Document Title",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = documentTitle,
                        onValueChange = { documentTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (language == AppLanguage.BANGLA) "ডকুমেন্ট বিবরণ ও টেক্সট" else "Document Body Content",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = documentBody,
                        onValueChange = { documentBody = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 10
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedStateButton(
                    text = if (language == AppLanguage.BANGLA) "পিডিএফ তৈরি করুন" else "Generate PDF",
                    onClick = { generatePdf() },
                    icon = Icons.Default.PictureAsPdf,
                    modifier = Modifier.weight(1f),
                    testTag = "generate_pdf_button"
                )

                if (generatedPdfFile != null) {
                    OutlinedStateButton(
                        text = if (language == AppLanguage.BANGLA) "শেয়ার করুন" else "Share File",
                        onClick = { sharePdf(generatedPdfFile!!) },
                        icon = Icons.Default.Share,
                        modifier = Modifier.weight(1f),
                        borderColor = MaterialTheme.colorScheme.primary,
                        testTag = "share_pdf_button"
                    )
                }
            }
        }

        if (generatedPdfFile != null) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = generatedPdfFile!!.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${generatedPdfFile!!.length() / 1024} KB • A4 Standard",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
