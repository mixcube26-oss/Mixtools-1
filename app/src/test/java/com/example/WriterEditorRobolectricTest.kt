package com.example

import android.content.Context
import android.os.Looper
import android.util.Base64
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MixToolsDatabase
import com.example.data.db.WriterDocumentEntity
import com.example.data.writer.WriterRepository
import com.example.model.AppLanguage
import com.example.model.ThemeMode
import com.example.ui.tools.writer.WriterBridge
import com.example.ui.tools.writer.WriterPageSize
import com.example.ui.tools.writer.WriterStats
import com.example.ui.tools.writer.WriterTemplateCatalog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.InputStreamReader

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WriterEditorRobolectricTest {

    private lateinit var database: MixToolsDatabase
    private lateinit var repository: WriterRepository
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, MixToolsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = WriterRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `verify document_editor html asset exists and has required viewport and engine structure`() {
        val assetStream = context.assets.open("writer/document_editor.html")
        assertNotNull(assetStream)
        val content = InputStreamReader(assetStream).use { it.readText() }

        // Must have pages-wrapper to prevent negative flex alignment
        assertTrue("Must contain pages-wrapper", content.contains("pages-wrapper"))
        assertTrue("Must contain pages-container", content.contains("pages-container"))
        assertTrue("Must contain page-sheet", content.contains("page-sheet"))
        assertTrue("Must contain editorArea", content.contains("editorArea"))

        // Must define CSS variables for light and dark themes
        assertTrue("Must contain --workspace-bg", content.contains("--workspace-bg"))
        assertTrue("Must contain --page-bg", content.contains("--page-bg"))
        assertTrue("Must contain --text-color", content.contains("--text-color"))
        assertTrue("Must contain dark-theme", content.contains("dark-theme"))

        // Must have public API methods
        assertTrue("Must define setContentBase64", content.contains("setContentBase64"))
        assertTrue("Must define fitWidth", content.contains("fitWidth"))
        assertTrue("Must define fitPage", content.contains("fitPage"))
        assertTrue("Must define resetCenter", content.contains("resetCenter"))
        assertTrue("Must define setDarkTheme", content.contains("setDarkTheme"))
        assertTrue("Must define setPageLayout", content.contains("setPageLayout"))
    }

    @Test
    fun `verify database save and restore lifecycle for Bangla and English documents`() = runBlocking {
        val banglaTitle = "নতুন প্রতিবেদন ২০২৬"
        val banglaContent = "<h1>বাংলা নথি</h1><p>এটি একটি সম্পূর্ণ পরীক্ষামূলক ডকুমেন্ট।</p>"
        val plainText = "বাংলা নথি\nএটি একটি সম্পূর্ণ পরীক্ষামূলক ডকুমেন্ট।"

        val newDoc = WriterDocumentEntity(
            title = banglaTitle,
            contentHtml = banglaContent,
            plainText = plainText,
            pageFormat = WriterPageSize.A4.idName,
            isLandscape = false,
            paperColorHex = "#FFFFFF",
            wordCount = 7,
            charCount = 52,
            pageCount = 1,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val id = repository.saveDocument(newDoc)
        assertTrue(id > 0)

        val retrieved = repository.getDocumentById(id)
        assertNotNull(retrieved)
        assertEquals(banglaTitle, retrieved?.title)
        assertEquals(banglaContent, retrieved?.contentHtml)
        assertEquals("#FFFFFF", retrieved?.paperColorHex)
        assertEquals(7, retrieved?.wordCount)

        // Test Base64 encoding used for safe WebView content restoration
        val base64 = Base64.encodeToString(retrieved!!.contentHtml.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        val decoded = String(Base64.decode(base64, Base64.DEFAULT), Charsets.UTF_8)
        assertEquals(banglaContent, decoded)
    }

    @Test
    fun `verify document updating and deleting in repository`() = runBlocking {
        val doc = WriterDocumentEntity(
            title = "Test Doc",
            contentHtml = "<p>Initial</p>",
            plainText = "Initial"
        )
        val id = repository.saveDocument(doc)
        val saved = repository.getDocumentById(id)!!

        val updated = saved.copy(
            title = "Renamed Doc",
            contentHtml = "<p>Updated Content</p>",
            wordCount = 2
        )
        repository.saveDocument(updated)

        val reloaded = repository.getDocumentById(id)
        assertEquals("Renamed Doc", reloaded?.title)
        assertEquals("<p>Updated Content</p>", reloaded?.contentHtml)
        assertEquals(2, reloaded?.wordCount)

        repository.deleteDocument(id)
        val afterDelete = repository.getDocumentById(id)
        assertNull(afterDelete)
    }

    @Test
    fun `verify WriterBridge delivers updates on Main Looper`() {
        var deliveredWords = -1
        var deliveredPlain = ""

        val bridge = WriterBridge(
            onContentUpdated = { _, plain, words, _, _ ->
                deliveredWords = words
                deliveredPlain = plain
            },
            onSelectionUpdated = { _, _, _, _, _, _, _, _, _, _, _, _, _ -> }
        )

        // Calling from background thread simulation
        bridge.onContentChanged("<p>Hello</p>", "Hello", 1, 5, 1)

        // Execute queued runnables on Main Looper
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(1, deliveredWords)
        assertEquals("Hello", deliveredPlain)
    }

    @Test
    fun `verify templates have non-empty initial HTML and valid default page sizes`() {
        val templatesEn = WriterTemplateCatalog.getTemplates(AppLanguage.ENGLISH)
        val templatesBn = WriterTemplateCatalog.getTemplates(AppLanguage.BANGLA)

        assertTrue(templatesEn.isNotEmpty())
        assertTrue(templatesBn.isNotEmpty())

        templatesEn.forEach {
            assertTrue(it.titleEn.isNotBlank())
            assertTrue(it.initialHtml.isNotBlank())
            assertNotNull(it.defaultFormat)
        }

        templatesBn.forEach {
            assertTrue(it.titleBn.isNotBlank())
            assertTrue(it.initialHtml.isNotBlank())
            assertNotNull(it.defaultFormat)
        }
    }
}
