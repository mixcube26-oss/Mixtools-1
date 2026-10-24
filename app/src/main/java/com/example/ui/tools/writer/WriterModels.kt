package com.example.ui.tools.writer

import com.example.model.AppLanguage

enum class WriterPageSize(
    val idName: String,
    val displayNameEn: String,
    val displayNameBn: String,
    val widthMm: Float,
    val heightMm: Float
) {
    A4("A4", "A4 (210 × 297 mm)", "A4 (২১০ × ২৯৭ মিমি)", 210f, 297f),
    A5("A5", "A5 (148 × 210 mm)", "A5 (১৪৮ × ২১০ মিমি)", 148f, 210f),
    LETTER("LETTER", "Letter (8.5 × 11 in)", "লেটার (৮.৫ × ১১ ইঞ্চি)", 215.9f, 279.4f),
    LEGAL("LEGAL", "Legal (8.5 × 14 in)", "লিগ্যাল (৮.৫ × ১৪ ইঞ্চি)", 215.9f, 355.6f),
    CUSTOM("CUSTOM", "Custom Size", "কাস্টম সাইজ", 210f, 297f);

    fun getDisplayName(language: AppLanguage): String {
        return if (language == AppLanguage.BANGLA) displayNameBn else displayNameEn
    }
}

enum class WriterMarginPreset(
    val displayNameEn: String,
    val displayNameBn: String,
    val topMm: Float,
    val bottomMm: Float,
    val leftMm: Float,
    val rightMm: Float
) {
    NORMAL("Normal (20 mm)", "স্বাভাবিক (২০ মিমি)", 20f, 20f, 20f, 20f),
    NARROW("Narrow (10 mm)", "সংকীর্ণ (১০ মিমি)", 10f, 10f, 10f, 10f),
    MODERATE("Moderate (15 mm)", "পরিমিত (১৫ মিমি)", 15f, 15f, 15f, 15f),
    WIDE("Wide (28 mm)", "প্রশস্ত (২৮ মিমি)", 28f, 28f, 28f, 28f);

    fun getDisplayName(language: AppLanguage): String {
        return if (language == AppLanguage.BANGLA) displayNameBn else displayNameEn
    }
}

data class WriterSelectionState(
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val isStrike: Boolean = false,
    val isSubscript: Boolean = false,
    val isSuperscript: Boolean = false,
    val alignment: String = "LEFT", // LEFT, CENTER, RIGHT, JUSTIFY
    val isBulletList: Boolean = false,
    val isNumberedList: Boolean = false,
    val fontName: String = "",
    val fontSize: String = "",
    val hasSelection: Boolean = false,
    val textDirection: String = "auto"
)

data class WriterStats(
    val wordCount: Int = 0,
    val charCount: Int = 0,
    val pageCount: Int = 1,
    val estimatedReadingTimeMinutes: Int = 1
)

enum class RibbonTab(val titleEn: String, val titleBn: String) {
    HOME("Home", "হোম"),
    INSERT("Insert", "ইনসার্ট"),
    LAYOUT("Layout", "লেআউট"),
    REVIEW("Review", "রিভিউ ও স্ট্যাটস")
}

data class DocumentStarterTemplate(
    val titleEn: String,
    val titleBn: String,
    val descEn: String,
    val descBn: String,
    val defaultFormat: WriterPageSize,
    val initialHtml: String
)

object WriterTemplateCatalog {
    fun getTemplates(language: AppLanguage): List<DocumentStarterTemplate> {
        return listOf(
            DocumentStarterTemplate(
                titleEn = "Blank Document",
                titleBn = "খালি নথি",
                descEn = "Clean blank page for writing",
                descBn = "লেখার জন্য খালি পেজ",
                defaultFormat = WriterPageSize.A4,
                initialHtml = "<p><br></p>"
            ),
            DocumentStarterTemplate(
                titleEn = "Official Application",
                titleBn = "অফিসিয়াল দরখাস্ত",
                descEn = "Formal letter and application format",
                descBn = "প্রমিত আবেদনপত্র ও চিঠির কাঠামো",
                defaultFormat = WriterPageSize.A4,
                initialHtml = if (language == AppLanguage.BANGLA) """
                    <p>তারিখ: ২০ অক্টোবর, ২০২৬</p>
                    <p>বরাবর,<br>যথাযথ কর্তৃপক্ষ<br>ঢাকা, বাংলাদেশ।</p>
                    <p><strong>বিষয়: ছুটির জন্য আবেদন।</strong></p>
                    <p>মহোদয়,<br>বিনীত নিবেদন এই যে, আমার ব্যক্তিগত জরুরি কারণে আগামী ... তারিখ হতে ... তারিখ পর্যন্ত উপস্থিত থাকা সম্ভব নয়।</p>
                    <p>অতএব, প্রার্থনা এই যে আমাকে উক্ত দিনগুলোর ছুটি দানে বাধিত করবেন।</p>
                    <br>
                    <p>বিনীত,<br><strong>আপনার বিশ্বস্ত</strong><br>[নাম]</p>
                """.trimIndent() else """
                    <p>Date: October 20, 2026</p>
                    <p>To,<br>The Manager / Department Head<br>City, Country.</p>
                    <p><strong>Subject: Formal Application for Leave.</strong></p>
                    <p>Dear Sir/Madam,</p>
                    <p>With due respect, I am writing to formally request leave of absence from [Start Date] to [End Date] due to personal commitments.</p>
                    <p>I will ensure all pending responsibilities are covered appropriately prior to my leave.</p>
                    <br>
                    <p>Sincerely yours,<br><strong>[Your Name]</strong><br>[Title]</p>
                """.trimIndent()
            ),
            DocumentStarterTemplate(
                titleEn = "Project Report",
                titleBn = "প্রজেক্ট রিপোর্ট",
                descEn = "Structured layout with headings and table",
                descBn = "শিরোনাম ও টেবিল সম্বলিত পূর্ণাঙ্গ রিপোর্ট",
                defaultFormat = WriterPageSize.A4,
                initialHtml = if (language == AppLanguage.BANGLA) """
                    <h1>প্রজেক্ট কার্যবিবরণী রিপোর্ট</h1>
                    <hr>
                    <h2>১. ভূমিকা ও উদ্দেশ্য</h2>
                    <p>এই নথিতে প্রকল্পের সার্বিক উদ্দেশ্য, বাস্তবায়ন পরিকল্পনা ও অগ্রগতি তুলে ধরা হলো।</p>
                    <h2>২. মূল অগ্রগতি ও মাইলফলক</h2>
                    <table class="doc-table">
                        <thead>
                            <tr><th>বিবরণ</th><th>সময়সীমা</th><th>অবস্থা</th></tr>
                        </thead>
                        <tbody>
                            <tr><td>পরিকল্পনা ও খসড়া</td><td>১ম পর্যায়</td><td>সম্পন্ন</td></tr>
                            <tr><td>বাস্তবায়ন ও পরীক্ষণ</td><td>২য় পর্যায়</td><td>চলমান</td></tr>
                            <tr><td>চূড়ান্ত ডেলিভারি</td><td>৩য় পর্যায়</td><td>প্রক্রিয়াধীন</td></tr>
                        </tbody>
                    </table>
                """.trimIndent() else """
                    <h1>Executive Project Report</h1>
                    <hr>
                    <h2>1. Executive Summary</h2>
                    <p>This document presents a comprehensive summary of our recent operational milestones, deliverables, and projected timelines.</p>
                    <h2>2. Key Deliverables</h2>
                    <table class="doc-table">
                        <thead>
                            <tr><th>Deliverable</th><th>Timeline</th><th>Status</th></tr>
                        </thead>
                        <tbody>
                            <tr><td>Requirements & Planning</td><td>Q1</td><td>Completed</td></tr>
                            <tr><td>Implementation & QA</td><td>Q2</td><td>In Progress</td></tr>
                            <tr><td>Final Delivery</td><td>Q3</td><td>Scheduled</td></tr>
                        </tbody>
                    </table>
                """.trimIndent()
            ),
            DocumentStarterTemplate(
                titleEn = "Meeting Minutes",
                titleBn = "মিটিং মিনিটস",
                descEn = "Agenda, attendees, and action items",
                descBn = "এজেন্ডা, উপস্থিত সদস্য ও সিদ্ধান্তসমূহ",
                defaultFormat = WriterPageSize.LETTER,
                initialHtml = if (language == AppLanguage.BANGLA) """
                    <h2>সভা কার্যবিবরণী</h2>
                    <p><strong>তারিখ ও সময়:</strong> ২০ অক্টোবর, ২০২৬ | ১০:০০ পূর্বাহ্ণ<br><strong>স্থান:</strong> কনফারেন্স রুম</p>
                    <hr>
                    <h3>১. উপস্থিত সদস্যবৃন্দ</h3>
                    <p>১. টিম লিড<br>২. প্রজেক্ট ম্যানেজার<br>৩. কোঅর্ডিনেটর</p>
                    <h3>২. আলোচিত বিষয় ও সিদ্ধান্ত</h3>
                    <ul>
                        <li>ত্রৈমাসিক লক্ষ্যমাত্রা পর্যালোচনা করা হলো।</li>
                        <li>পরবর্তী কর্মপরিকল্পনা ও সময়সীমা অনুমোদন করা হলো।</li>
                    </ul>
                """.trimIndent() else """
                    <h2>Meeting Minutes</h2>
                    <p><strong>Date & Time:</strong> October 20, 2026 | 10:00 AM<br><strong>Location:</strong> Conference Room</p>
                    <hr>
                    <h3>1. Attendees</h3>
                    <p>1. Team Lead<br>2. Project Manager<br>3. Coordinator</p>
                    <h3>2. Discussion Points & Decisions</h3>
                    <ul>
                        <li>Reviewed quarterly project goals and milestones.</li>
                        <li>Approved operational workflow and delivery schedule.</li>
                    </ul>
                """.trimIndent()
            )
        )
    }
}
