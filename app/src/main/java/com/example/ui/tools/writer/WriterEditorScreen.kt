package com.example.ui.tools.writer

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Base64
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.FormatIndentDecrease
import androidx.compose.material.icons.automirrored.filled.FormatIndentIncrease
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatClear
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InsertPageBreak
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subscript
import androidx.compose.material.icons.filled.Superscript
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.data.db.WriterDocumentEntity
import com.example.model.AppLanguage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import com.example.model.ThemeMode
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriterEditorScreen(
    language: AppLanguage,
    savedDocuments: List<WriterDocumentEntity>,
    onSaveDocument: (WriterDocumentEntity, ((Long) -> Unit)?) -> Unit,
    onDeleteDocument: (Long) -> Unit,
    onBackToApp: () -> Unit,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Screen mode: Document Studio Hub vs. Direct Page Workspace Editor
    var activeDocument by remember { mutableStateOf<WriterDocumentEntity?>(null) }
    var isNewDocDialogVisible by remember { mutableStateOf(false) }

    // Dialog & Sheet States for Editor
    var isPageSetupDialogVisible by remember { mutableStateOf(false) }
    var isTableDialogVisible by remember { mutableStateOf(false) }
    var isFindReplaceVisible by remember { mutableStateOf(false) }
    var isStatsDialogVisible by remember { mutableStateOf(false) }
    var isRenameDialogVisible by remember { mutableStateOf(false) }
    var isColorPaletteVisible by remember { mutableStateOf(false) }
    var isHighlightPaletteVisible by remember { mutableStateOf(false) }
    var isFontSizeMenuVisible by remember { mutableStateOf(false) }
    var isFontFamilyMenuVisible by remember { mutableStateOf(false) }
    var isHeadingsMenuVisible by remember { mutableStateOf(false) }
    var isMoreMenuExpanded by remember { mutableStateOf(false) }

    // WebView Reference & Bridge State
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentHtml by remember { mutableStateOf("") }
    var currentPlainText by remember { mutableStateOf("") }
    var liveStats by remember { mutableStateOf(WriterStats()) }
    var selectionState by remember { mutableStateOf(WriterSelectionState()) }
    var currentZoomScale by remember { mutableFloatStateOf(1.0f) }
    var showMarginGuidelines by remember { mutableStateOf(true) }
    var saveStatusText by remember { mutableStateOf(if (language == AppLanguage.BANGLA) "সংরক্ষিত" else "Saved") }
    var isDirty by remember { mutableStateOf(false) }

    // Ribbon Tab
    var selectedRibbonTab by remember { mutableStateOf(RibbonTab.HOME) }

    // Find & Replace state
    var searchQuery by remember { mutableStateOf("") }
    var replaceQuery by remember { mutableStateOf("") }
    var searchMatchesCount by remember { mutableIntStateOf(0) }

    // Viewport Pan/Zoom & Focus States
    var isPanMode by remember { mutableStateOf(false) }
    var isNavigationPadVisible by remember { mutableStateOf(false) }
    var isDistractionFree by remember { mutableStateOf(false) }
    var isRibbonCollapsed by remember { mutableStateOf(false) }

    // Helper to evaluate JS in WebView
    fun execJs(script: String) {
        webViewInstance?.post {
            webViewInstance?.evaluateJavascript(script, null)
        }
    }

    fun setPanMode(enabled: Boolean) {
        isPanMode = enabled
        execJs("window.writerEngine.setPanMode($enabled);")
        if (enabled) {
            isNavigationPadVisible = true
        }
    }

    fun panViewport(direction: String) {
        execJs("window.writerEngine.panBy('$direction');")
    }

    fun zoomInViewport() {
        execJs("window.writerEngine.zoomIn();")
    }

    fun zoomOutViewport() {
        execJs("window.writerEngine.zoomOut();")
    }

    fun fitWidthViewport() {
        execJs("window.writerEngine.fitWidth();")
    }

    fun fitPageViewport() {
        execJs("window.writerEngine.fitPage();")
    }

    fun resetCenterViewport() {
        execJs("window.writerEngine.resetCenter();")
    }

    // Auto-save function
    fun persistCurrentDocument(onComplete: ((Long) -> Unit)? = null) {
        val doc = activeDocument ?: return
        val updated = doc.copy(
            contentHtml = currentHtml.ifEmpty { doc.contentHtml },
            plainText = currentPlainText.ifEmpty { doc.plainText },
            wordCount = liveStats.wordCount,
            charCount = liveStats.charCount,
            pageCount = liveStats.pageCount,
            updatedAt = System.currentTimeMillis()
        )
        saveStatusText = if (language == AppLanguage.BANGLA) "সংরক্ষণ হচ্ছে..." else "Saving..."
        onSaveDocument(updated) { newId ->
            if (activeDocument != null) {
                activeDocument = updated.copy(id = newId)
            }
            saveStatusText = if (language == AppLanguage.BANGLA) "সংরক্ষিত" else "Saved"
            isDirty = false
            onComplete?.invoke(newId)
        }
    }

    // Safe exit editor function (saves if dirty, does not resurrect activeDocument)
    fun exitEditor() {
        val doc = activeDocument
        if (doc != null && isDirty) {
            val updated = doc.copy(
                contentHtml = currentHtml.ifEmpty { doc.contentHtml },
                plainText = currentPlainText.ifEmpty { doc.plainText },
                wordCount = liveStats.wordCount,
                charCount = liveStats.charCount,
                pageCount = liveStats.pageCount,
                updatedAt = System.currentTimeMillis()
            )
            onSaveDocument(updated, null)
            Toast.makeText(
                context,
                if (language == AppLanguage.BANGLA) "নথি সংরক্ষিত হয়েছে" else "Document saved",
                Toast.LENGTH_SHORT
            ).show()
        }
        activeDocument = null
        isDirty = false
    }

    // Photo picker launcher for Insert Image
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (originalBitmap != null) {
                    // Scale down image to reasonable dimensions for document embedding
                    val maxDimension = 900
                    val scale = Math.min(
                        maxDimension.toFloat() / originalBitmap.width,
                        maxDimension.toFloat() / originalBitmap.height
                    ).coerceAtMost(1.0f)

                    val scaledWidth = (originalBitmap.width * scale).toInt()
                    val scaledHeight = (originalBitmap.height * scale).toInt()
                    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true)

                    val outputStream = ByteArrayOutputStream()
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                    val base64Data = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                    val dataUrl = "data:image/jpeg;base64,$base64Data"

                    execJs("window.writerEngine.insertImage('$dataUrl');")
                    isDirty = true
                    Toast.makeText(
                        context,
                        if (language == AppLanguage.BANGLA) "ছবি সংযুক্ত হয়েছে" else "Image inserted into document",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error inserting image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Import .mixtoolsdoc Project Launcher
    val importProjectLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val existingTitles = savedDocuments.map { it.title }.toSet()
            when (val result = MixToolsDocPackage.importProject(context, uri, existingTitles)) {
                is MixToolsDocPackage.ImportResult.Success -> {
                    onSaveDocument(result.document) { newId ->
                        activeDocument = result.document.copy(id = newId)
                        currentHtml = result.document.contentHtml
                        currentPlainText = result.document.plainText
                    }
                    val msg = if (result.isRenamed) {
                        if (language == AppLanguage.BANGLA)
                            "প্রজেক্ট ইমপোর্ট হয়েছে (নতুন কপি হিসেবে)"
                        else
                            "Project imported as copy: ${result.document.title}"
                    } else {
                        if (language == AppLanguage.BANGLA)
                            "প্রজেক্ট সফলভাবে খোলা হয়েছে"
                        else
                            "Project loaded successfully"
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
                is MixToolsDocPackage.ImportResult.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Share Editable .mixtoolsdoc Project
    fun shareCurrentProject() {
        val doc = activeDocument ?: return
        persistCurrentDocument {
            val fullDoc = doc.copy(
                contentHtml = currentHtml.ifEmpty { doc.contentHtml },
                plainText = currentPlainText.ifEmpty { doc.plainText },
                wordCount = liveStats.wordCount,
                charCount = liveStats.charCount,
                pageCount = liveStats.pageCount
            )
            MixToolsDocPackage.shareProject(context, fullDoc)
        }
    }

    // Print & PDF Export via Android PrintManager
    fun triggerPrintDocument() {
        val doc = activeDocument ?: return
        val webView = webViewInstance ?: return
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            val jobName = "MixTools_${doc.title.replace("\\s+".toRegex(), "_")}"
            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            val printAttributes = PrintAttributes.Builder()
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMediaSize(
                    if (doc.isLandscape) PrintAttributes.MediaSize.ISO_A4.asLandscape()
                    else PrintAttributes.MediaSize.ISO_A4.asPortrait()
                )
                .build()

            printManager?.print(jobName, printAdapter, printAttributes)
        } catch (e: Exception) {
            Toast.makeText(context, "Print error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Share Plain Text
    fun shareDocumentText() {
        val doc = activeDocument ?: return
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, doc.title)
            putExtra(Intent.EXTRA_TEXT, "${doc.title}\n\n${currentPlainText}")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Document via"))
    }

    // Copy Plain Text
    fun copyDocumentText() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("MixTools Document", currentPlainText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            context,
            if (language == AppLanguage.BANGLA) "সম্পূর্ণ টেক্সট কপি করা হয়েছে" else "Document text copied to clipboard",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Handle Back Press with full hierarchy priority
    BackHandler {
        when {
            isMoreMenuExpanded -> isMoreMenuExpanded = false
            isFontSizeMenuVisible -> isFontSizeMenuVisible = false
            isFontFamilyMenuVisible -> isFontFamilyMenuVisible = false
            isHeadingsMenuVisible -> isHeadingsMenuVisible = false
            isFindReplaceVisible -> isFindReplaceVisible = false
            isColorPaletteVisible -> isColorPaletteVisible = false
            isHighlightPaletteVisible -> isHighlightPaletteVisible = false
            isTableDialogVisible -> isTableDialogVisible = false
            isPageSetupDialogVisible -> isPageSetupDialogVisible = false
            isRenameDialogVisible -> isRenameDialogVisible = false
            isNewDocDialogVisible -> isNewDocDialogVisible = false
            isStatsDialogVisible -> isStatsDialogVisible = false
            isDistractionFree -> isDistractionFree = false
            isPanMode -> {
                setPanMode(false)
                isNavigationPadVisible = false
            }
            activeDocument != null -> exitEditor()
            else -> onBackToApp()
        }
    }

    // ==========================================
    // VIEW 1: DOCUMENT STUDIO / MANAGER VIEW
    // ==========================================
    if (activeDocument == null) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "ডকুমেন্ট রাইটার" else "Document Writer",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackToApp,
                            modifier = Modifier.testTag("writer_hub_back_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        OutlinedButton(
                            onClick = {
                                importProjectLauncher.launch(arrayOf("*/*"))
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "ইমপোর্ট" else "Import",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isNewDocDialogVisible = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("fab_create_document")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "নতুন নথি" else "New Document",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Starter Templates Section
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) "টেমপ্লেটসমূহ" else "Templates",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val templates = WriterTemplateCatalog.getTemplates(language)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        templates.forEach { tmpl ->
                            Card(
                                onClick = {
                                    val newDoc = WriterDocumentEntity(
                                        title = if (language == AppLanguage.BANGLA) tmpl.titleBn else tmpl.titleEn,
                                        contentHtml = tmpl.initialHtml,
                                        pageFormat = tmpl.defaultFormat.idName,
                                        createdAt = System.currentTimeMillis(),
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    onSaveDocument(newDoc) { id ->
                                        activeDocument = newDoc.copy(id = id)
                                        currentHtml = tmpl.initialHtml
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(14.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (language == AppLanguage.BANGLA) tmpl.titleBn else tmpl.titleEn,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = if (language == AppLanguage.BANGLA) tmpl.descBn else tmpl.descEn,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surface
                                    ) {
                                        Text(
                                            text = tmpl.defaultFormat.idName,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Saved Documents List
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "সংরক্ষিত নথিপত্র (${savedDocuments.size})"
                            else
                                "Saved Documents (${savedDocuments.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (savedDocuments.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(32.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA)
                                        "এখনো কোনো সংরক্ষিত নথি নেই"
                                    else
                                        "No saved documents yet",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA)
                                        "+ চেপে নতুন নথি শুরু করুন"
                                    else
                                        "Tap + to start a new document",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(savedDocuments) { doc ->
                        val dateFormatted = remember(doc.updatedAt) {
                            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(doc.updatedAt))
                        }

                        Card(
                            onClick = {
                                activeDocument = doc
                                currentHtml = doc.contentHtml
                                currentPlainText = doc.plainText
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = doc.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "$dateFormatted • ${doc.pageFormat} • ${doc.wordCount} words",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteDocument(doc.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (doc.plainText.isNotBlank()) doc.plainText.take(120) + "..." else "(Empty document content)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FilledTonalButton(
                                        onClick = {
                                            activeDocument = doc
                                            currentHtml = doc.contentHtml
                                            currentPlainText = doc.plainText
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (language == AppLanguage.BANGLA) "খুলুন" else "Open Editor")
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // ==========================================
        // CREATE NEW DOCUMENT SETUP MODAL / DIALOG
        // ==========================================
        if (isNewDocDialogVisible) {
            var newTitle by remember {
                mutableStateOf(if (language == AppLanguage.BANGLA) "নতুন নথি ${savedDocuments.size + 1}" else "Untitled Document ${savedDocuments.size + 1}")
            }
            var selectedSize by remember { mutableStateOf(WriterPageSize.A4) }
            var isLandscape by remember { mutableStateOf(false) }
            var selectedMargin by remember { mutableStateOf(WriterMarginPreset.NORMAL) }

            AlertDialog(
                onDismissRequest = { isNewDocDialogVisible = false },
                title = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "নতুন নথি" else "New Document",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text(if (language == AppLanguage.BANGLA) "নথির নাম" else "Document Title") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = if (language == AppLanguage.BANGLA) "পেজ সাইজ:" else "Paper Size:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(WriterPageSize.A4, WriterPageSize.LETTER, WriterPageSize.LEGAL, WriterPageSize.A5).forEach { size ->
                                FilterChip(
                                    selected = selectedSize == size,
                                    onClick = { selectedSize = size },
                                    label = { Text(size.idName, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Text(
                            text = if (language == AppLanguage.BANGLA) "অরিয়েন্টেশন:" else "Orientation:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = !isLandscape,
                                onClick = { isLandscape = false },
                                label = { Text(if (language == AppLanguage.BANGLA) "পোর্ট্রেট" else "Portrait") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = isLandscape,
                                onClick = { isLandscape = true },
                                label = { Text(if (language == AppLanguage.BANGLA) "ল্যান্ডস্কেপ" else "Landscape") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Text(
                            text = if (language == AppLanguage.BANGLA) "মার্জিন:" else "Margins:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(WriterMarginPreset.NORMAL, WriterMarginPreset.NARROW, WriterMarginPreset.WIDE).forEach { m ->
                                FilterChip(
                                    selected = selectedMargin == m,
                                    onClick = { selectedMargin = m },
                                    label = { Text(m.getDisplayName(language).substringBefore(" "), fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val entity = WriterDocumentEntity(
                                title = newTitle.ifBlank { "Document" },
                                contentHtml = "<p><br></p>",
                                plainText = "",
                                pageFormat = selectedSize.idName,
                                isLandscape = isLandscape,
                                marginTopMm = selectedMargin.topMm,
                                marginBottomMm = selectedMargin.bottomMm,
                                marginLeftMm = selectedMargin.leftMm,
                                marginRightMm = selectedMargin.rightMm,
                                createdAt = System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                            )
                            onSaveDocument(entity) { id ->
                                activeDocument = entity.copy(id = id)
                                currentHtml = entity.contentHtml
                                currentPlainText = ""
                                isNewDocDialogVisible = false
                            }
                        }
                    ) {
                        Text(if (language == AppLanguage.BANGLA) "তৈরি করুন" else "Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isNewDocDialogVisible = false }) {
                        Text(if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel")
                    }
                }
            )
        }
        return
    }

    // ==========================================
    // VIEW 2: DIRECT PAGE WORKSPACE EDITOR
    // ==========================================
    val currentDoc = activeDocument!!

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (!isDistractionFree) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // Compact Top Bar (single back button!)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Single Back Button
                            IconButton(
                                onClick = { exitEditor() },
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("writer_editor_back_button")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Documents",
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Document Title & Live Save Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { isRenameDialogVisible = true }
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f, fill = false)) {
                                    Text(
                                        text = currentDoc.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (saveStatusText.contains("Saving") || saveStatusText.contains("হচ্ছে"))
                                                        Color(0xFFF59E0B)
                                                    else
                                                        Color(0xFF10B981)
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "$saveStatusText • ${currentDoc.pageFormat}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Rename",
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            }

                            // Undo
                            IconButton(
                                onClick = { execJs("window.writerEngine.exec('undo');") },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", modifier = Modifier.size(18.dp))
                            }
                            // Redo
                            IconButton(
                                onClick = { execJs("window.writerEngine.exec('redo');") },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", modifier = Modifier.size(18.dp))
                            }
                            // Mode Toggle: Write vs Pan
                            IconButton(
                                onClick = { setPanMode(!isPanMode) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPanMode) Icons.Default.PanTool else Icons.Default.Edit,
                                    contentDescription = "Toggle Pan Mode",
                                    tint = if (isPanMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            // Distraction-Free Toggle
                            IconButton(
                                onClick = { isDistractionFree = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.Fullscreen,
                                    contentDescription = "Distraction-Free Mode",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            // Save
                            IconButton(
                                onClick = { persistCurrentDocument() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.Save,
                                    contentDescription = "Save",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            // More
                            Box {
                                IconButton(
                                    onClick = { isMoreMenuExpanded = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(18.dp))
                                }
                                DropdownMenu(
                                    expanded = isMoreMenuExpanded,
                                    onDismissRequest = { isMoreMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(if (language == AppLanguage.BANGLA) "পেজ সেটআপ" else "Page Setup") },
                                        leadingIcon = { Icon(Icons.Default.Layers, contentDescription = null) },
                                        onClick = {
                                            isMoreMenuExpanded = false
                                            isPageSetupDialogVisible = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(if (language == AppLanguage.BANGLA) "ভিউ সেন্টার" else "Center View") },
                                        leadingIcon = { Icon(Icons.Default.CenterFocusStrong, contentDescription = null) },
                                        onClick = {
                                            isMoreMenuExpanded = false
                                            resetCenterViewport()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(if (language == AppLanguage.BANGLA) "প্রিন্ট" else "Print") },
                                        leadingIcon = { Icon(Icons.Default.Print, contentDescription = null) },
                                        onClick = {
                                            isMoreMenuExpanded = false
                                            triggerPrintDocument()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(if (language == AppLanguage.BANGLA) "পিডিএফ এক্সপোর্ট" else "Export PDF") },
                                        leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null) },
                                        onClick = {
                                            isMoreMenuExpanded = false
                                            triggerPrintDocument()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(if (language == AppLanguage.BANGLA) "প্রজেক্ট শেয়ার (.mixtoolsdoc)" else "Share Project (.mixtoolsdoc)") },
                                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                        onClick = {
                                            isMoreMenuExpanded = false
                                            shareCurrentProject()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(if (language == AppLanguage.BANGLA) "টেক্সট শেয়ার" else "Share Plain Text") },
                                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                        onClick = {
                                            isMoreMenuExpanded = false
                                            shareDocumentText()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(if (language == AppLanguage.BANGLA) "টেক্সট কপি" else "Copy Text") },
                                        leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                                        onClick = {
                                            isMoreMenuExpanded = false
                                            copyDocumentText()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(if (language == AppLanguage.BANGLA) "পরিসংখ্যান" else "Statistics") },
                                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                                        onClick = {
                                            isMoreMenuExpanded = false
                                            isStatsDialogVisible = true
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Word-Style Ribbon Tabs with Collapse Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ScrollableTabRow(
                            selectedTabIndex = selectedRibbonTab.ordinal,
                            edgePadding = 8.dp,
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        ) {
                            RibbonTab.values().forEach { tab ->
                                Tab(
                                    selected = selectedRibbonTab == tab,
                                    onClick = {
                                        selectedRibbonTab = tab
                                        isRibbonCollapsed = false
                                    },
                                    text = {
                                        Text(
                                            text = if (language == AppLanguage.BANGLA) tab.titleBn else tab.titleEn,
                                            fontWeight = if (selectedRibbonTab == tab) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                    }
                                )
                            }
                        }

                        // Collapse / Expand Ribbon Toggle
                        IconButton(
                            onClick = { isRibbonCollapsed = !isRibbonCollapsed },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = if (isRibbonCollapsed) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                                contentDescription = "Toggle Ribbon Strip",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Ribbon Actions Strip
                    AnimatedVisibility(visible = !isRibbonCollapsed) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (selectedRibbonTab) {
                            RibbonTab.HOME -> {
                                // Formatting Ribbon
                                // Font Family Picker
                                Box {
                                    OutlinedButton(
                                        onClick = { isFontFamilyMenuVisible = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("Font", fontSize = 12.sp)
                                    }
                                    DropdownMenu(
                                        expanded = isFontFamilyMenuVisible,
                                        onDismissRequest = { isFontFamilyMenuVisible = false }
                                    ) {
                                        listOf(
                                            "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif" to "System / Modern Sans",
                                            "SolaimanLipi, 'Noto Sans Bengali', Arial, sans-serif" to "SolaimanLipi (বাংলা)",
                                            "Kalpurush, 'Noto Sans Bengali', Arial, sans-serif" to "Kalpurush (বাংলা)",
                                            "Amiri, 'Noto Naskh Arabic', Arial, sans-serif" to "Amiri (العربية / Arabic)",
                                            "Georgia, serif" to "Georgia (Serif)",
                                            "Times New Roman, serif" to "Times New Roman",
                                            "Courier New, monospace" to "Courier New (Mono)",
                                            "Arial, sans-serif" to "Arial"
                                        ).forEach { (font, label) ->
                                            DropdownMenuItem(
                                                text = { Text(label) },
                                                onClick = {
                                                    isFontFamilyMenuVisible = false
                                                    execJs("window.writerEngine.setFontFamily('$font');")
                                                }
                                            )
                                        }
                                    }
                                }

                                // Font Size Selector
                                Box {
                                    OutlinedButton(
                                        onClick = { isFontSizeMenuVisible = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(Icons.Default.FormatSize, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (selectionState.fontSize.isNotBlank()) "${selectionState.fontSize}pt" else "12pt", fontSize = 12.sp)
                                    }
                                    DropdownMenu(
                                        expanded = isFontSizeMenuVisible,
                                        onDismissRequest = { isFontSizeMenuVisible = false }
                                    ) {
                                        listOf(9, 10, 11, 12, 14, 16, 18, 20, 24, 28, 32, 36).forEach { pt ->
                                            DropdownMenuItem(
                                                text = { Text("$pt pt") },
                                                onClick = {
                                                    isFontSizeMenuVisible = false
                                                    execJs("window.writerEngine.setFontSizePt($pt);")
                                                }
                                            )
                                        }
                                    }
                                }

                                // Headings Selector
                                Box {
                                    OutlinedButton(
                                        onClick = { isHeadingsMenuVisible = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("Style", fontSize = 12.sp)
                                    }
                                    DropdownMenu(
                                        expanded = isHeadingsMenuVisible,
                                        onDismissRequest = { isHeadingsMenuVisible = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Normal Text") },
                                            onClick = {
                                                isHeadingsMenuVisible = false
                                                execJs("window.writerEngine.setHeading('p');")
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Heading 1") },
                                            onClick = {
                                                isHeadingsMenuVisible = false
                                                execJs("window.writerEngine.setHeading('h1');")
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Heading 2") },
                                            onClick = {
                                                isHeadingsMenuVisible = false
                                                execJs("window.writerEngine.setHeading('h2');")
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Heading 3") },
                                            onClick = {
                                                isHeadingsMenuVisible = false
                                                execJs("window.writerEngine.setHeading('h3');")
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Blockquote") },
                                            onClick = {
                                                isHeadingsMenuVisible = false
                                                execJs("window.writerEngine.setHeading('blockquote');")
                                            }
                                        )
                                    }
                                }

                                RibbonDivider()

                                // Bold
                                RibbonIconButton(
                                    icon = Icons.Default.FormatBold,
                                    isActive = selectionState.isBold,
                                    onClick = { execJs("window.writerEngine.exec('bold');") }
                                )
                                // Italic
                                RibbonIconButton(
                                    icon = Icons.Default.FormatItalic,
                                    isActive = selectionState.isItalic,
                                    onClick = { execJs("window.writerEngine.exec('italic');") }
                                )
                                // Underline
                                RibbonIconButton(
                                    icon = Icons.Default.FormatUnderlined,
                                    isActive = selectionState.isUnderline,
                                    onClick = { execJs("window.writerEngine.exec('underline');") }
                                )
                                // Strikethrough
                                RibbonIconButton(
                                    icon = Icons.Default.FormatStrikethrough,
                                    isActive = selectionState.isStrike,
                                    onClick = { execJs("window.writerEngine.exec('strikeThrough');") }
                                )
                                // Subscript
                                RibbonIconButton(
                                    icon = Icons.Default.Subscript,
                                    isActive = selectionState.isSubscript,
                                    onClick = { execJs("window.writerEngine.exec('subscript');") }
                                )
                                // Superscript
                                RibbonIconButton(
                                    icon = Icons.Default.Superscript,
                                    isActive = selectionState.isSuperscript,
                                    onClick = { execJs("window.writerEngine.exec('superscript');") }
                                )

                                RibbonDivider()

                                // Text Color
                                RibbonIconButton(
                                    icon = Icons.Default.FormatColorText,
                                    onClick = { isColorPaletteVisible = true }
                                )
                                // Text Highlight
                                RibbonIconButton(
                                    icon = Icons.Default.FormatColorFill,
                                    onClick = { isHighlightPaletteVisible = true }
                                )
                                // Clear Formatting
                                RibbonIconButton(
                                    icon = Icons.Default.FormatClear,
                                    onClick = { execJs("window.writerEngine.clearFormatting();") }
                                )

                                RibbonDivider()

                                // Align Left
                                RibbonIconButton(
                                    icon = Icons.AutoMirrored.Filled.FormatAlignLeft,
                                    isActive = selectionState.alignment == "LEFT",
                                    onClick = { execJs("window.writerEngine.exec('justifyLeft');") }
                                )
                                // Align Center
                                RibbonIconButton(
                                    icon = Icons.Default.FormatAlignCenter,
                                    isActive = selectionState.alignment == "CENTER",
                                    onClick = { execJs("window.writerEngine.exec('justifyCenter');") }
                                )
                                // Align Right
                                RibbonIconButton(
                                    icon = Icons.AutoMirrored.Filled.FormatAlignRight,
                                    isActive = selectionState.alignment == "RIGHT",
                                    onClick = { execJs("window.writerEngine.exec('justifyRight');") }
                                )
                                // Align Justify
                                RibbonIconButton(
                                    icon = Icons.Default.FormatAlignJustify,
                                    isActive = selectionState.alignment == "JUSTIFY",
                                    onClick = { execJs("window.writerEngine.exec('justifyFull');") }
                                )

                                RibbonDivider()

                                // Bullet List
                                RibbonIconButton(
                                    icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                                    isActive = selectionState.isBulletList,
                                    onClick = { execJs("window.writerEngine.exec('insertUnorderedList');") }
                                )
                                // Numbered List
                                RibbonIconButton(
                                    icon = Icons.Default.FormatListNumbered,
                                    isActive = selectionState.isNumberedList,
                                    onClick = { execJs("window.writerEngine.exec('insertOrderedList');") }
                                )
                                // Indent Decrease / Increase
                                RibbonIconButton(
                                    icon = Icons.AutoMirrored.Filled.FormatIndentDecrease,
                                    onClick = { execJs("window.writerEngine.exec('outdent');") }
                                )
                                RibbonIconButton(
                                    icon = Icons.AutoMirrored.Filled.FormatIndentIncrease,
                                    onClick = { execJs("window.writerEngine.exec('indent');") }
                                )

                                RibbonDivider()

                                // Direction Controls
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectionState.textDirection == "ltr") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, if (selectionState.textDirection == "ltr") MaterialTheme.colorScheme.primary else Color.Transparent),
                                    modifier = Modifier.clickable { execJs("window.writerEngine.setTextDirection('ltr');") }
                                ) {
                                    Text(
                                        text = "LTR",
                                        fontSize = 11.sp,
                                        fontWeight = if (selectionState.textDirection == "ltr") FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectionState.textDirection == "ltr") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectionState.textDirection == "rtl") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, if (selectionState.textDirection == "rtl") MaterialTheme.colorScheme.primary else Color.Transparent),
                                    modifier = Modifier.clickable { execJs("window.writerEngine.setTextDirection('rtl');") }
                                ) {
                                    Text(
                                        text = "RTL",
                                        fontSize = 11.sp,
                                        fontWeight = if (selectionState.textDirection == "rtl") FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectionState.textDirection == "rtl") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectionState.textDirection == "auto") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, if (selectionState.textDirection == "auto") MaterialTheme.colorScheme.primary else Color.Transparent),
                                    modifier = Modifier.clickable { execJs("window.writerEngine.setTextDirection('auto');") }
                                ) {
                                    Text(
                                        text = "Auto",
                                        fontSize = 11.sp,
                                        fontWeight = if (selectionState.textDirection == "auto") FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectionState.textDirection == "auto") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            RibbonTab.INSERT -> {
                                // Insert Objects Ribbon
                                OutlinedButton(
                                    onClick = { isTableDialogVisible = true },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (language == AppLanguage.BANGLA) "টেবিল" else "Table", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (language == AppLanguage.BANGLA) "ছবি" else "Image", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { execJs("window.writerEngine.insertPageBreak();") },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.InsertPageBreak, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (language == AppLanguage.BANGLA) "পেজ ব্রেক" else "Page Break", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { execJs("window.writerEngine.insertHorizontalRule();") },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.HorizontalRule, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (language == AppLanguage.BANGLA) "রেখা" else "Line", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { execJs("window.writerEngine.insertDateTime();") },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (language == AppLanguage.BANGLA) "তারিখ" else "Date", fontSize = 12.sp)
                                }
                            }

                            RibbonTab.LAYOUT -> {
                                // Layout Settings Ribbon
                                OutlinedButton(
                                    onClick = { isPageSetupDialogVisible = true },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (language == AppLanguage.BANGLA) "পেজ সেটআপ" else "Page Setup", fontSize = 12.sp)
                                }

                                FilterChip(
                                    selected = currentDoc.isLandscape,
                                    onClick = {
                                        val newLandscape = !currentDoc.isLandscape
                                        activeDocument = currentDoc.copy(isLandscape = newLandscape)
                                        execJs(
                                             "window.writerEngine.setPageLayout('${currentDoc.pageFormat}', $newLandscape, ${currentDoc.marginTopMm}, ${currentDoc.marginBottomMm}, ${currentDoc.marginLeftMm}, ${currentDoc.marginRightMm}, '${currentDoc.paperColorHex}');"
                                        )
                                        isDirty = true
                                    },
                                    label = { Text(if (currentDoc.isLandscape) "Landscape" else "Portrait", fontSize = 12.sp) }
                                )

                                FilterChip(
                                    selected = showMarginGuidelines,
                                    onClick = {
                                        showMarginGuidelines = !showMarginGuidelines
                                        execJs("window.writerEngine.toggleMargins($showMarginGuidelines);")
                                    },
                                    label = { Text(if (language == AppLanguage.BANGLA) "মার্জিন" else "Margins", fontSize = 12.sp) }
                                )

                                OutlinedButton(
                                    onClick = {
                                        execJs("window.writerEngine.setLineSpacing('1.0');")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("1.0", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        execJs("window.writerEngine.setLineSpacing('1.5');")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("1.5", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        execJs("window.writerEngine.setLineSpacing('2.0');")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("2.0", fontSize = 12.sp)
                                }
                            }

                            RibbonTab.REVIEW -> {
                                // Review and Stats
                                OutlinedButton(
                                    onClick = { isFindReplaceVisible = !isFindReplaceVisible },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.FindReplace, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (language == AppLanguage.BANGLA) "অনুসন্ধান" else "Find", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { isStatsDialogVisible = true },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${liveStats.wordCount} words • ${liveStats.pageCount} p", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

                // Find & Replace Floating / Expandable Bar
                AnimatedVisibility(visible = isFindReplaceVisible) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = {
                                        searchQuery = it
                                        val escaped = it.replace("'", "\\'")
                                        execJs("window.writerEngine.findAndReplace('$escaped', null, true);")
                                    },
                                    placeholder = { Text(if (language == AppLanguage.BANGLA) "খোঁজার শব্দ..." else "Find text...") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = replaceQuery,
                                    onValueChange = { replaceQuery = it },
                                    placeholder = { Text(if (language == AppLanguage.BANGLA) "নতুন শব্দ..." else "Replace with...") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { isFindReplaceVisible = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilledTonalButton(
                                    onClick = {
                                        val sEsc = searchQuery.replace("'", "\\'")
                                        val rEsc = replaceQuery.replace("'", "\\'")
                                        execJs("window.writerEngine.findAndReplace('$sEsc', '$rEsc', false);")
                                    }
                                ) {
                                    Text(if (language == AppLanguage.BANGLA) "একটি পরিবর্তন" else "Replace")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val sEsc = searchQuery.replace("'", "\\'")
                                        val rEsc = replaceQuery.replace("'", "\\'")
                                        execJs("window.writerEngine.findAndReplace('$sEsc', '$rEsc', true);")
                                    }
                                ) {
                                    Text(if (language == AppLanguage.BANGLA) "সব পরিবর্তন" else "Replace All")
                                }
                            }
                        }
                    }
                }
            }
        }
    },
    bottomBar = {
        if (!isDistractionFree) {
            // Live Status Bar at bottom
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "পৃষ্ঠা ${liveStats.pageCount} • ${liveStats.wordCount} শব্দ • ${liveStats.charCount} বর্ণ"
                        else
                            "Page ${liveStats.pageCount} • ${liveStats.wordCount} words • ${liveStats.charCount} chars",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${currentDoc.pageFormat} • ${if (currentDoc.isLandscape) (if (language == AppLanguage.BANGLA) "ল্যান্ডস্কেপ" else "Landscape") else (if (language == AppLanguage.BANGLA) "পোর্ট্রেট" else "Portrait")}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
    ) { paddingValues ->
        val isDarkTheme = when (themeMode) {
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
        }
        val workspaceBgColor = if (isDarkTheme) Color(0xFF0B1120) else Color(0xFFE2E8F0)

        LaunchedEffect(isDarkTheme) {
            execJs("if (window.writerEngine && window.writerEngine.setDarkTheme) window.writerEngine.setDarkTheme($isDarkTheme);")
        }

        // ==========================================
        // REAL PAGINATED WHITE PAPER SURFACE VIA WEBVIEW
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(workspaceBgColor)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("writer_page_webview"),
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewInstance = this
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            allowFileAccess = true
                            allowContentAccess = true
                            useWideViewPort = false
                            loadWithOverviewMode = false
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        }

                        webChromeClient = object : android.webkit.WebChromeClient() {
                            override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                                android.util.Log.d(
                                    "MixToolsWriterJS",
                                    "${consoleMessage?.message()} [${consoleMessage?.sourceId()}:${consoleMessage?.lineNumber()}]"
                                )
                                return true
                            }
                        }

                        // Register 2-way JavaScript Interface
                        val bridge = WriterBridge(
                            onContentUpdated = { html, plain, words, chars, pages ->
                                currentHtml = html
                                currentPlainText = plain
                                liveStats = WriterStats(
                                    wordCount = words,
                                    charCount = chars,
                                    pageCount = pages,
                                    estimatedReadingTimeMinutes = Math.max(1, (words / 200.0).toInt())
                                )
                                isDirty = true
                            },
                            onSelectionUpdated = { bold, italic, underline, strike, sub, sup, align, bList, nList, fName, fSize, hasSel, dir ->
                                selectionState = WriterSelectionState(
                                    isBold = bold,
                                    isItalic = italic,
                                    isUnderline = underline,
                                    isStrike = strike,
                                    isSubscript = sub,
                                    isSuperscript = sup,
                                    alignment = align,
                                    isBulletList = bList,
                                    isNumberedList = nList,
                                    fontName = fName,
                                    fontSize = fSize,
                                    hasSelection = hasSel,
                                    textDirection = dir
                                )
                            },
                            onViewportUpdated = { zoom, _, _ ->
                                currentZoomScale = zoom
                            }
                        )
                        addJavascriptInterface(bridge, "AndroidBridge")

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                val doc = activeDocument ?: currentDoc
                                val escapedTitle = doc.title.replace("'", "\\'")
                                evaluateJavascript("if (window.writerEngine) window.writerEngine.setDocTitle('$escapedTitle');", null)
                                evaluateJavascript(
                                    "if (window.writerEngine) window.writerEngine.setPageLayout('${doc.pageFormat}', ${doc.isLandscape}, ${doc.marginTopMm}, ${doc.marginBottomMm}, ${doc.marginLeftMm}, ${doc.marginRightMm}, '${doc.paperColorHex}');",
                                    null
                                )
                                evaluateJavascript("if (window.writerEngine) window.writerEngine.setDarkTheme($isDarkTheme);", null)
                                if (doc.contentHtml.isNotBlank()) {
                                    val base64Html = Base64.encodeToString(doc.contentHtml.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
                                    evaluateJavascript("if (window.writerEngine) window.writerEngine.setContentBase64('$base64Html');", null)
                                }
                                evaluateJavascript("if (window.writerEngine) window.writerEngine.fitWidth();", null)
                            }

                            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                                super.onReceivedError(view, errorCode, description, failingUrl)
                                android.util.Log.e("MixToolsWriter", "WebView error: $description ($errorCode) at $failingUrl")
                            }
                        }

                        loadUrl("file:///android_asset/writer/document_editor.html")
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                }
            )

            // If Distraction-Free mode is active, show floating minimal control pill
            if (isDistractionFree) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                    shadowElevation = 6.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { isDistractionFree = false },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.FullscreenExit, contentDescription = "Exit Distraction-Free", modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { execJs("window.writerEngine.exec('undo');") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { execJs("window.writerEngine.exec('redo');") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { setPanMode(!isPanMode) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isPanMode) Icons.Default.PanTool else Icons.Default.Edit,
                                contentDescription = "Toggle Pan Mode",
                                modifier = Modifier.size(16.dp),
                                tint = if (isPanMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { persistCurrentDocument() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Floating Navigation Pad or Minimized Pill
            if (isNavigationPadVisible) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 12.dp)
                ) {
                    ViewportNavigationPad(
                        currentZoom = currentZoomScale,
                        isPanMode = isPanMode,
                        language = language,
                        onPan = { dir -> panViewport(dir) },
                        onZoomIn = { zoomInViewport() },
                        onZoomOut = { zoomOutViewport() },
                        onFitWidth = { fitWidthViewport() },
                        onFitPage = { fitPageViewport() },
                        onResetCenter = { resetCenterViewport() },
                        onTogglePanMode = { setPanMode(!isPanMode) },
                        onClose = {
                            isNavigationPadVisible = false
                            if (isPanMode) setPanMode(false)
                        }
                    )
                }
            } else {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 12.dp)
                        .clickable {
                            isNavigationPadVisible = true
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPanMode) Icons.Default.PanTool else Icons.Default.FitScreen,
                            contentDescription = "Viewport Controls",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${(currentZoomScale * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Floating Contextual Formatting Toolbar (shown when user selects text)
            AnimatedVisibility(
                visible = selectionState.hasSelection && !isPanMode,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (isDistractionFree) 20.dp else 40.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        IconButton(
                            onClick = { execJs("window.writerEngine.exec('bold');") },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.FormatBold,
                                contentDescription = "Bold",
                                tint = if (selectionState.isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(
                            onClick = { execJs("window.writerEngine.exec('italic');") },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.FormatItalic,
                                contentDescription = "Italic",
                                tint = if (selectionState.isItalic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(
                            onClick = { execJs("window.writerEngine.exec('underline');") },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.FormatUnderlined,
                                contentDescription = "Underline",
                                tint = if (selectionState.isUnderline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(
                            onClick = { isColorPaletteVisible = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.FormatColorText,
                                contentDescription = "Text Color",
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(
                            onClick = { isHighlightPaletteVisible = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.FormatColorFill,
                                contentDescription = "Highlight",
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(
                            onClick = { isFontSizeMenuVisible = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.FormatSize,
                                contentDescription = "Size",
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                val nextDir = if (selectionState.textDirection == "rtl") "ltr" else "rtl"
                                execJs("window.writerEngine.setTextDirection('$nextDir');")
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Text(
                                text = if (selectionState.textDirection == "rtl") "RTL" else "LTR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = { execJs("window.writerEngine.clearFormatting();") },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.FormatClear,
                                contentDescription = "Clear",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // MODAL DIALOGS FOR RICH-TEXT OPERATIONS
    // ==========================================

    // Rename Document Dialog
    if (isRenameDialogVisible) {
        var tempTitle by remember { mutableStateOf(currentDoc.title) }
        AlertDialog(
            onDismissRequest = { isRenameDialogVisible = false },
            title = { Text(if (language == AppLanguage.BANGLA) "নাম পরিবর্তন" else "Rename Document") },
            text = {
                OutlinedTextField(
                    value = tempTitle,
                    onValueChange = { tempTitle = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val updated = currentDoc.copy(title = tempTitle.ifBlank { "Document" })
                    activeDocument = updated
                    isRenameDialogVisible = false
                    execJs("window.writerEngine.setDocTitle('${updated.title.replace("'", "\\'")}');")
                    persistCurrentDocument()
                }) {
                    Text(if (language == AppLanguage.BANGLA) "সংরক্ষণ" else "Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { isRenameDialogVisible = false }) {
                    Text(if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel")
                }
            }
        )
    }

    // Page Setup and Margins Dialog
    if (isPageSetupDialogVisible) {
        var tempSize by remember {
            mutableStateOf(
                runCatching { WriterPageSize.valueOf(currentDoc.pageFormat) }.getOrDefault(WriterPageSize.A4)
            )
        }
        var tempLandscape by remember { mutableStateOf(currentDoc.isLandscape) }
        var tempMargin by remember { mutableStateOf(WriterMarginPreset.NORMAL) }

        AlertDialog(
            onDismissRequest = { isPageSetupDialogVisible = false },
            title = { Text(if (language == AppLanguage.BANGLA) "পেজ সেটআপ" else "Page Setup") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "কাগজের মাপ:" else "Paper Size:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(WriterPageSize.A4, WriterPageSize.LETTER, WriterPageSize.LEGAL, WriterPageSize.A5).forEach { sz ->
                            FilterChip(
                                selected = tempSize == sz,
                                onClick = { tempSize = sz },
                                label = { Text(sz.idName, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Text(
                        text = if (language == AppLanguage.BANGLA) "অরিয়েন্টেশন:" else "Orientation:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = !tempLandscape,
                            onClick = { tempLandscape = false },
                            label = { Text(if (language == AppLanguage.BANGLA) "পোর্ট্রেট" else "Portrait", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = tempLandscape,
                            onClick = { tempLandscape = true },
                            label = { Text(if (language == AppLanguage.BANGLA) "ল্যান্ডস্কেপ" else "Landscape", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = if (language == AppLanguage.BANGLA) "মার্জিন:" else "Margins:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(WriterMarginPreset.NORMAL, WriterMarginPreset.NARROW, WriterMarginPreset.WIDE).forEach { mp ->
                            FilterChip(
                                selected = tempMargin == mp,
                                onClick = { tempMargin = mp },
                                label = { Text(mp.getDisplayName(language).substringBefore(" "), fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val updated = currentDoc.copy(
                        pageFormat = tempSize.idName,
                        isLandscape = tempLandscape,
                        marginTopMm = tempMargin.topMm,
                        marginBottomMm = tempMargin.bottomMm,
                        marginLeftMm = tempMargin.leftMm,
                        marginRightMm = tempMargin.rightMm
                    )
                    activeDocument = updated
                    isPageSetupDialogVisible = false
                    execJs(
                        "window.writerEngine.setPageLayout('${updated.pageFormat}', ${updated.isLandscape}, ${updated.marginTopMm}, ${updated.marginBottomMm}, ${updated.marginLeftMm}, ${updated.marginRightMm}, '${updated.paperColorHex}');"
                    )
                    persistCurrentDocument()
                }) {
                    Text(if (language == AppLanguage.BANGLA) "প্রয়োগ" else "Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { isPageSetupDialogVisible = false }) {
                    Text(if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel")
                }
            }
        )
    }

    // Insert Table Dialog
    if (isTableDialogVisible) {
        var rows by remember { mutableIntStateOf(3) }
        var cols by remember { mutableIntStateOf(3) }

        AlertDialog(
            onDismissRequest = { isTableDialogVisible = false },
            title = { Text(if (language == AppLanguage.BANGLA) "টেবিল" else "Insert Table") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(if (language == AppLanguage.BANGLA) "সারি: $rows" else "Rows: $rows", fontWeight = FontWeight.SemiBold)
                    Slider(
                        value = rows.toFloat(),
                        onValueChange = { rows = it.toInt() },
                        valueRange = 1f..8f,
                        steps = 6
                    )

                    Text(if (language == AppLanguage.BANGLA) "কলাম: $cols" else "Columns: $cols", fontWeight = FontWeight.SemiBold)
                    Slider(
                        value = cols.toFloat(),
                        onValueChange = { cols = it.toInt() },
                        valueRange = 1f..6f,
                        steps = 4
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    isTableDialogVisible = false
                    execJs("window.writerEngine.insertTable($rows, $cols);")
                }) {
                    Text(if (language == AppLanguage.BANGLA) "যোগ করুন" else "Insert")
                }
            },
            dismissButton = {
                TextButton(onClick = { isTableDialogVisible = false }) {
                    Text(if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel")
                }
            }
        )
    }

    // Color Palette Dialog
    if (isColorPaletteVisible) {
        AlertDialog(
            onDismissRequest = { isColorPaletteVisible = false },
            title = { Text(if (language == AppLanguage.BANGLA) "টেক্সট কালার" else "Text Color") },
            text = {
                val colors = listOf(
                    "#0F172A" to "Charcoal",
                    "#DC2626" to "Crimson Red",
                    "#2563EB" to "Royal Blue",
                    "#16A34A" to "Emerald Green",
                    "#D97706" to "Amber Gold",
                    "#9333EA" to "Vibrant Purple",
                    "#475569" to "Slate Gray"
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.chunked(4).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            row.forEach { (hex, _) ->
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(hex)))
                                        .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                                        .clickable {
                                            isColorPaletteVisible = false
                                            execJs("window.writerEngine.setTextColor('$hex');")
                                        }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { isColorPaletteVisible = false }) {
                    Text(if (language == AppLanguage.BANGLA) "বন্ধ" else "Close")
                }
            }
        )
    }

    // Highlight Palette Dialog
    if (isHighlightPaletteVisible) {
        AlertDialog(
            onDismissRequest = { isHighlightPaletteVisible = false },
            title = { Text(if (language == AppLanguage.BANGLA) "হাইলাইট" else "Highlight") },
            text = {
                val highlights = listOf(
                    "#FEF08A" to "Yellow",
                    "#BBF7D0" to "Mint Green",
                    "#BAE6FD" to "Sky Blue",
                    "#FED7AA" to "Peach",
                    "#FBCFE8" to "Pink",
                    "transparent" to "Clear"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    highlights.forEach { (hex, _) ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (hex == "transparent") Color.LightGray.copy(alpha = 0.5f) else Color(android.graphics.Color.parseColor(hex)))
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .clickable {
                                    isHighlightPaletteVisible = false
                                    execJs("window.writerEngine.setHighlightColor('$hex');")
                                }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { isHighlightPaletteVisible = false }) {
                    Text(if (language == AppLanguage.BANGLA) "বন্ধ" else "Close")
                }
            }
        )
    }

    // Document Statistics Dialog
    if (isStatsDialogVisible) {
        AlertDialog(
            onDismissRequest = { isStatsDialogVisible = false },
            title = { Text(if (language == AppLanguage.BANGLA) "পরিসংখ্যান" else "Document Statistics") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatRow(label = if (language == AppLanguage.BANGLA) "মোট শব্দ" else "Total Words", value = "${liveStats.wordCount}")
                    StatRow(label = if (language == AppLanguage.BANGLA) "মোট বর্ণ" else "Characters", value = "${liveStats.charCount}")
                    StatRow(label = if (language == AppLanguage.BANGLA) "পৃষ্ঠা সংখ্যা" else "Estimated Pages", value = "${liveStats.pageCount}")
                    StatRow(
                        label = if (language == AppLanguage.BANGLA) "পড়ার সময়" else "Reading Time",
                        value = "${liveStats.estimatedReadingTimeMinutes} min"
                    )
                    StatRow(
                        label = if (language == AppLanguage.BANGLA) "কাগজের মাপ" else "Paper Size",
                        value = "${currentDoc.pageFormat} (${if (currentDoc.isLandscape) (if (language == AppLanguage.BANGLA) "ল্যান্ডস্কেপ" else "Landscape") else (if (language == AppLanguage.BANGLA) "পোর্ট্রেট" else "Portrait")})"
                    )
                }
            },
            confirmButton = {
                Button(onClick = { isStatsDialogVisible = false }) {
                    Text(if (language == AppLanguage.BANGLA) "ঠিক আছে" else "OK")
                }
            }
        )
    }
}

@Composable
private fun RibbonIconButton(
    icon: ImageVector,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = if (isActive) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        border = if (isActive) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun RibbonDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(24.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    )
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
    }
}
