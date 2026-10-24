package com.example.ui.tools.writer

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.db.WriterDocumentEntity
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Native MixTools editable project package (.mixtoolsdoc) handler.
 * Encapsulates full export, sharing via Android Sharesheet (FileProvider),
 * and safe offline import with zip-slip prevention and schema validation.
 */
object MixToolsDocPackage {

    const val EXTENSION = ".mixtoolsdoc"
    const val MIME_TYPE = "application/octet-stream"
    const val CURRENT_SCHEMA_VERSION = 1
    const val FORMAT_ID = "mixtoolsdoc"

    sealed class ImportResult {
        data class Success(val document: WriterDocumentEntity, val isRenamed: Boolean) : ImportResult()
        data class Error(val message: String) : ImportResult()
    }

    /**
     * Packages a WriterDocumentEntity into a versioned .mixtoolsdoc ZIP archive.
     */
    fun exportProject(context: Context, document: WriterDocumentEntity): File {
        val projectsDir = File(context.cacheDir, "projects").apply {
            if (!exists()) mkdirs()
        }

        val sanitizedTitle = document.title
            .trim()
            .replace("[^a-zA-Z0-9._-]".toRegex(), "_")
            .ifBlank { "Untitled_Document" }

        val projectFile = File(projectsDir, "$sanitizedTitle$EXTENSION")
        if (projectFile.exists()) {
            projectFile.delete()
        }

        // Build Manifest JSON
        val manifestObj = JSONObject().apply {
            put("format", FORMAT_ID)
            put("schemaVersion", CURRENT_SCHEMA_VERSION)
            put("appVersion", "1.0.0")
            put("title", document.title)
            put("pageFormat", document.pageFormat)
            put("isLandscape", document.isLandscape)
            put("marginTopMm", document.marginTopMm.toDouble())
            put("marginBottomMm", document.marginBottomMm.toDouble())
            put("marginLeftMm", document.marginLeftMm.toDouble())
            put("marginRightMm", document.marginRightMm.toDouble())
            put("paperColorHex", document.paperColorHex)
            put("wordCount", document.wordCount)
            put("charCount", document.charCount)
            put("pageCount", document.pageCount)
            put("createdAt", document.createdAt)
            put("updatedAt", document.updatedAt)
            put("exportedAt", System.currentTimeMillis())
        }

        ZipOutputStream(FileOutputStream(projectFile)).use { zos ->
            // 1. Write manifest.json
            val manifestEntry = ZipEntry("manifest.json")
            zos.putNextEntry(manifestEntry)
            zos.write(manifestObj.toString(2).toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()

            // 2. Write content.html
            val htmlEntry = ZipEntry("content.html")
            zos.putNextEntry(htmlEntry)
            zos.write(document.contentHtml.toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()

            // 3. Write content.txt
            val txtEntry = ZipEntry("content.txt")
            zos.putNextEntry(txtEntry)
            zos.write(document.plainText.toByteArray(StandardCharsets.UTF_8))
            zos.closeEntry()
        }

        return projectFile
    }

    /**
     * Shares an editable .mixtoolsdoc project file via Android Sharesheet.
     * Operates 100% offline using standard Android FileProvider.
     */
    fun shareProject(context: Context, document: WriterDocumentEntity) {
        val file = exportProject(context, document)
        val authority = "${context.packageName}.fileprovider"
        val contentUri: Uri = FileProvider.getUriForFile(context, authority, file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = MIME_TYPE
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "${document.title}$EXTENSION")
            putExtra(
                Intent.EXTRA_TEXT,
                "MixTools Document Project: ${document.title} (${document.pageFormat})"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, "Share Project (.mixtoolsdoc)").apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(chooser)
    }

    /**
     * Validates and imports a .mixtoolsdoc project archive from an external Uri.
     * Guaranteed safe against Zip-Slip path traversal attacks.
     * Generates a unique non-conflicting title so existing projects are never overwritten.
     */
    fun importProject(
        context: Context,
        uri: Uri,
        existingTitles: Set<String>
    ): ImportResult {
        var manifestContent: String? = null
        var htmlContent: String? = null
        var textContent: String? = null

        val inputStream: InputStream = context.contentResolver.openInputStream(uri)
            ?: return ImportResult.Error("Unable to open selected file stream")

        try {
            ZipInputStream(inputStream).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                var entryCount = 0
                val maxEntries = 50

                while (entry != null) {
                    entryCount++
                    if (entryCount > maxEntries) {
                        return ImportResult.Error("Archive exceeds safe entry limit")
                    }

                    val name = entry.name
                    // Zip Slip Path Traversal Check
                    if (name.contains("..") || name.startsWith("/") || name.startsWith("\\")) {
                        return ImportResult.Error("Security validation failed: Invalid archive entry path")
                    }

                    val buffer = ByteArrayOutputStream()
                    val chunk = ByteArray(4096)
                    var bytesRead: Int
                    var totalEntryBytes = 0
                    val maxEntryBytes = 15 * 1024 * 1024 // 15 MB limit per entry

                    while (zis.read(chunk).also { bytesRead = it } != -1) {
                        totalEntryBytes += bytesRead
                        if (totalEntryBytes > maxEntryBytes) {
                            return ImportResult.Error("Archive entry exceeds maximum allowable size")
                        }
                        buffer.write(chunk, 0, bytesRead)
                    }

                    val entryText = buffer.toString(StandardCharsets.UTF_8.name())
                    when (name) {
                        "manifest.json" -> manifestContent = entryText
                        "content.html" -> htmlContent = entryText
                        "content.txt" -> textContent = entryText
                    }

                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        } catch (e: Exception) {
            return ImportResult.Error("Error reading project archive: ${e.message}")
        }

        if (manifestContent == null) {
            return ImportResult.Error("Corrupt or invalid project: missing manifest.json")
        }

        return try {
            val manifest = JSONObject(manifestContent!!)
            val format = manifest.optString("format", "")
            if (format != FORMAT_ID && !manifest.has("schemaVersion")) {
                return ImportResult.Error("Unsupported project format or unverified document archive")
            }

            val schemaVersion = manifest.optInt("schemaVersion", 1)
            if (schemaVersion > CURRENT_SCHEMA_VERSION) {
                return ImportResult.Error("This document was created with a newer version of MixTools (v$schemaVersion)")
            }

            val originalTitle = manifest.optString("title", "Imported Document").trim()
            var uniqueTitle = originalTitle.ifBlank { "Imported Document" }
            var isRenamed = false

            // Ensure non-destructive naming
            if (existingTitles.contains(uniqueTitle)) {
                var counter = 1
                var candidate = "$uniqueTitle (Copy)"
                while (existingTitles.contains(candidate)) {
                    counter++
                    candidate = "$uniqueTitle (Copy $counter)"
                }
                uniqueTitle = candidate
                isRenamed = true
            }

            val document = WriterDocumentEntity(
                id = 0, // Auto-generate new primary key
                title = uniqueTitle,
                contentHtml = htmlContent ?: "<p><br></p>",
                plainText = textContent ?: "",
                pageFormat = manifest.optString("pageFormat", "A4"),
                isLandscape = manifest.optBoolean("isLandscape", false),
                marginTopMm = manifest.optDouble("marginTopMm", 20.0).toFloat(),
                marginBottomMm = manifest.optDouble("marginBottomMm", 20.0).toFloat(),
                marginLeftMm = manifest.optDouble("marginLeftMm", 20.0).toFloat(),
                marginRightMm = manifest.optDouble("marginRightMm", 20.0).toFloat(),
                paperColorHex = manifest.optString("paperColorHex", "#FFFFFF"),
                wordCount = manifest.optInt("wordCount", 0),
                charCount = manifest.optInt("charCount", 0),
                pageCount = manifest.optInt("pageCount", 1),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            ImportResult.Success(document, isRenamed)
        } catch (e: Exception) {
            ImportResult.Error("Failed to parse project metadata: ${e.message}")
        }
    }
}
