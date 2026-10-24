package com.example.data.writer

import com.example.data.db.MixToolsDatabase
import com.example.data.db.WriterDocumentEntity
import kotlinx.coroutines.flow.Flow

class WriterRepository(private val database: MixToolsDatabase) {

    fun getAllDocuments(): Flow<List<WriterDocumentEntity>> {
        return database.writerDocumentDao().getAllDocuments()
    }

    suspend fun getDocumentById(id: Long): WriterDocumentEntity? {
        return database.writerDocumentDao().getDocumentById(id)
    }

    suspend fun saveDocument(document: WriterDocumentEntity): Long {
        return database.writerDocumentDao().saveDocument(document)
    }

    suspend fun deleteDocument(id: Long) {
        database.writerDocumentDao().deleteDocument(id)
    }

    suspend fun renameDocument(id: Long, newTitle: String) {
        database.writerDocumentDao().renameDocument(id, newTitle)
    }

    suspend fun createInitialTemplateDocument(
        title: String,
        pageFormat: String = "A4",
        isLandscape: Boolean = false,
        topMm: Float = 20f,
        bottomMm: Float = 20f,
        leftMm: Float = 20f,
        rightMm: Float = 20f,
        paperColorHex: String = "#FFFFFF",
        initialHtml: String? = null
    ): Long {
        val defaultContent = initialHtml ?: """
            <h1>$title</h1>
            <p>Welcome to <strong>MixTools Writer</strong>, your mobile Microsoft Word-style document editor. Tap anywhere on this sheet to write, select text to format, insert tables, add images, and export to PDF.</p>
            <p><br></p>
        """.trimIndent()

        val entity = WriterDocumentEntity(
            title = title,
            contentHtml = defaultContent,
            plainText = "$title\nWelcome to MixTools Writer.",
            pageFormat = pageFormat,
            isLandscape = isLandscape,
            marginTopMm = topMm,
            marginBottomMm = bottomMm,
            marginLeftMm = leftMm,
            marginRightMm = rightMm,
            paperColorHex = paperColorHex,
            wordCount = 28,
            charCount = 185,
            pageCount = 1,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return saveDocument(entity)
    }
}
