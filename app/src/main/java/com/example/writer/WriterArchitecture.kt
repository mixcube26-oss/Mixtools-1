package com.example.writer

import java.io.File

/**
 * Standard page dimensions in PostScript points (1/72 inch).
 */
enum class PageFormat(val displayName: String, val widthPt: Float, val heightPt: Float) {
    A4("A4 (210 × 297 mm)", 595.28f, 841.89f),
    A5("A5 (148 × 210 mm)", 419.53f, 595.28f),
    LETTER("Letter (8.5 × 11 in)", 612f, 792f),
    LEGAL("Legal (8.5 × 14 in)", 612f, 1008f),
    CUSTOM("Custom Size", 595.28f, 841.89f);

    val aspectRatio: Float
        get() = widthPt / heightPt
}

data class PageMargins(
    val topPt: Float = 54f,     // 0.75 inch
    val bottomPt: Float = 54f,
    val leftPt: Float = 54f,
    val rightPt: Float = 54f
)

enum class TextAlignment {
    LEFT,
    CENTER,
    RIGHT,
    JUSTIFY
}

data class FontDescriptor(
    val fontFamilyName: String,
    val isSystemFont: Boolean = true,
    val customFontFile: File? = null
)

data class FormattingSpan(
    val start: Int,
    val end: Int,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val isStrikethrough: Boolean = false,
    val fontSizeSp: Float = 16f,
    val textColorHex: Long = 0xFFFFFFFF,
    val backgroundColorHex: Long? = null,
    val fontName: String = "SansSerif"
)

data class ParagraphBlock(
    val text: String,
    val alignment: TextAlignment = TextAlignment.LEFT,
    val spans: List<FormattingSpan> = emptyList(),
    val bulletLevel: Int = 0,
    val isNumbered: Boolean = false,
    val numberIndex: Int? = null
)

data class DocumentMetadata(
    val id: String,
    val title: String,
    val author: String = "MixTools User",
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),
    val version: Int = 1
)

data class DocumentProject(
    val metadata: DocumentMetadata,
    val pageFormat: PageFormat = PageFormat.A4,
    val margins: PageMargins = PageMargins(),
    val paragraphs: List<ParagraphBlock> = listOf(ParagraphBlock("")),
    val headerText: String? = null,
    val footerText: String? = null
)

interface DocumentExporter {
    fun exportToPdf(project: DocumentProject, destinationFile: File): Result<File>
    fun exportToPlainText(project: DocumentProject): String
}
