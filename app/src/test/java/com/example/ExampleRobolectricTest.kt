package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ToolRegistry
import com.example.model.CategoryId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MixTools", appName)
    }

    @Test
    fun `verify all 8 required categories exist in registry`() {
        val categories = ToolRegistry.categories
        assertEquals(8, categories.size)
        assertTrue(categories.any { it.id == CategoryId.PHONE_MANAGER })
        assertTrue(categories.any { it.id == CategoryId.CALCULATORS })
        assertTrue(categories.any { it.id == CategoryId.WRITER_TOOLS })
        assertTrue(categories.any { it.id == CategoryId.IMAGE_TOOLS })
        assertTrue(categories.any { it.id == CategoryId.PDF_TOOLS })
        assertTrue(categories.any { it.id == CategoryId.QR_TOOLS })
        assertTrue(categories.any { it.id == CategoryId.VIDEO_TOOLS })
        assertTrue(categories.any { it.id == CategoryId.TEXT_TOOLS })
    }

    @Test
    fun `search returns tools for English and Bangla queries`() {
        val enResults = ToolRegistry.searchTools("calculator")
        assertFalse(enResults.isEmpty())

        val bnResults = ToolRegistry.searchTools("ক্যালকুলেটর")
        assertFalse(bnResults.isEmpty())

        val textResults = ToolRegistry.searchTools("টেক্সট")
        assertFalse(textResults.isEmpty())
    }
}
