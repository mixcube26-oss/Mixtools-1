package com.example.ui.tools.writer

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface

class WriterBridge(
    private val onContentUpdated: (html: String, plainText: String, words: Int, chars: Int, pages: Int) -> Unit,
    private val onSelectionUpdated: (
        isBold: Boolean,
        isItalic: Boolean,
        isUnderline: Boolean,
        isStrike: Boolean,
        isSubscript: Boolean,
        isSuperscript: Boolean,
        align: String,
        isBulletList: Boolean,
        isNumberedList: Boolean,
        fontName: String,
        fontSize: String,
        hasSelection: Boolean,
        textDirection: String
    ) -> Unit,
    private val onViewportUpdated: ((zoom: Float, panX: Float, panY: Float) -> Unit)? = null
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onContentChanged(html: String, plainText: String, words: Int, chars: Int, pages: Int) {
        mainHandler.post {
            onContentUpdated(html, plainText, words, chars, pages)
        }
    }

    @JavascriptInterface
    fun onSelectionChanged(
        isBold: Boolean,
        isItalic: Boolean,
        isUnderline: Boolean,
        isStrike: Boolean,
        isSubscript: Boolean,
        isSuperscript: Boolean,
        align: String,
        isBulletList: Boolean,
        isNumberedList: Boolean,
        fontName: String,
        fontSize: String,
        hasSelection: Boolean = false,
        textDirection: String = "auto"
    ) {
        mainHandler.post {
            onSelectionUpdated(
                isBold, isItalic, isUnderline, isStrike,
                isSubscript, isSuperscript, align, isBulletList, isNumberedList,
                fontName, fontSize, hasSelection, textDirection
            )
        }
    }

    @JavascriptInterface
    fun onViewportChanged(zoom: Float, panX: Float, panY: Float) {
        mainHandler.post {
            onViewportUpdated?.invoke(zoom, panX, panY)
        }
    }
}

