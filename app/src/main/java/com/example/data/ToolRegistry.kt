package com.example.data

import com.example.model.CategoryId
import com.example.model.ToolCategory
import com.example.model.ToolItem

object ToolRegistry {

    val categories: List<ToolCategory> = listOf(
        ToolCategory(
            id = CategoryId.TEXT_TOOLS,
            titleEn = "Text Tools",
            titleBn = "টেক্সট টুলস",
            descriptionEn = "Edit, convert, and format your text easily.",
            descriptionBn = "সহজেই টেক্সট রূপান্তর, পরিমার্জন এবং ফরম্যাট করুন।",
            colorHex = 0xFF2563EB,
            iconName = "text_fields"
        ),
        ToolCategory(
            id = CategoryId.CALCULATORS,
            titleEn = "Calculators",
            titleBn = "ক্যালকুলেটর",
            descriptionEn = "Scientific calculation, unit conversions, and timers.",
            descriptionBn = "হিসাব-নিকাশ, একক রূপান্তর এবং সময় গণনা।",
            colorHex = 0xFFF97316,
            iconName = "calculate"
        ),
        ToolCategory(
            id = CategoryId.PHONE_MANAGER,
            titleEn = "Phone Manager",
            titleBn = "ফোন ম্যানেজার",
            descriptionEn = "Device specifications, storage, and battery telemetry.",
            descriptionBn = "ডিভাইস তথ্য, স্টোরেজ ও ব্যাটারি মনিটর।",
            colorHex = 0xFF8B5CF6,
            iconName = "phone_android"
        ),
        ToolCategory(
            id = CategoryId.WRITER_TOOLS,
            titleEn = "Writer Tools",
            titleBn = "রাইটার টুলস",
            descriptionEn = "Document drafting foundation with A4/A5 page support.",
            descriptionBn = "পৃষ্ঠা সাইজ ও ডকুমেন্ট খসড়া ব্যবস্থাপনা।",
            colorHex = 0xFF06B6D4,
            iconName = "edit_document"
        ),
        ToolCategory(
            id = CategoryId.IMAGE_TOOLS,
            titleEn = "Image Tools",
            titleBn = "ছবি টুলস",
            descriptionEn = "Inspect dimensions, extract palettes, and rotate pictures.",
            descriptionBn = "ছবির সাইজ পরিমাপ, রঙের প্যালেট এবং রোটেশন।",
            colorHex = 0xFFEC4899,
            iconName = "image"
        ),
        ToolCategory(
            id = CategoryId.PDF_TOOLS,
            titleEn = "PDF Tools",
            titleBn = "পিডিএফ টুলস",
            descriptionEn = "Generate real PDF documents from formatted text.",
            descriptionBn = "টেক্সট থেকে দ্রুত আসল পিডিএফ তৈরি ও শেয়ার করুন।",
            colorHex = 0xFFEF4444,
            iconName = "picture_as_pdf"
        ),
        ToolCategory(
            id = CategoryId.QR_TOOLS,
            titleEn = "QR Tools",
            titleBn = "কিউআর টুলস",
            descriptionEn = "Generate customized QR codes for URLs, WiFi, and contacts.",
            descriptionBn = "ওয়েবসাইট, ওয়াইফাই এবং তথ্যের জন্য কিউআর কোড।",
            colorHex = 0xFF10B981,
            iconName = "qr_code"
        ),
        ToolCategory(
            id = CategoryId.VIDEO_TOOLS,
            titleEn = "Video Tools",
            titleBn = "ভিডিও টুলস",
            descriptionEn = "Aspect ratios, frame rates, and bitrate estimators.",
            descriptionBn = "অ্যাসপেক্ট রেশিও এবং বিটরেট ক্যালকুলেটর।",
            colorHex = 0xFFA855F7,
            iconName = "videocam"
        )
    )

    val tools: List<ToolItem> = listOf(
        // TEXT TOOLS
        ToolItem(
            id = "tool_text_editor",
            categoryId = CategoryId.TEXT_TOOLS,
            titleEn = "Text Editor",
            titleBn = "টেক্সট এডিটর",
            descriptionEn = "Write, edit and format your text with live styling and counters.",
            descriptionBn = "রিয়েল-টাইম ফরম্যাটিং ও পরিসংখ্যান সহ টেক্সট লিখুন।",
            keywordsEn = list("text", "editor", "write", "note", "format", "typing"),
            keywordsBn = list("টেক্সট", "এডিটর", "লেখা", "নোট", "টাইপিং"),
            route = "tool/text_editor",
            isPopular = true
        ),
        ToolItem(
            id = "tool_text_counter",
            categoryId = CategoryId.TEXT_TOOLS,
            titleEn = "Text Counter",
            titleBn = "টেক্সট কাউন্টার",
            descriptionEn = "Live word, character, sentence, and reading/speaking time statistics.",
            descriptionBn = "শব্দ, অক্ষর, বাক্য, লাইন এবং পড়ার সময় লাইভ গণনা।",
            keywordsEn = list("text", "counter", "words", "characters", "sentences", "reading time", "speaking time"),
            keywordsBn = list("টেক্সট", "কাউন্টার", "শব্দ", "অক্ষর", "বাক্য", "গণনা"),
            route = "tool/text_counter",
            isPopular = true,
            isNew = true
        ),
        ToolItem(
            id = "tool_case_converter",
            categoryId = CategoryId.TEXT_TOOLS,
            titleEn = "Case Converter",
            titleBn = "কেস কনভার্টার",
            descriptionEn = "Change text case to UPPERCASE, lowercase, Title Case, camelCase.",
            descriptionBn = "বড় হাতের অক্ষর, ছোট হাতের অক্ষর বা টাইটেল কেসে রূপান্তর।",
            keywordsEn = list("case", "uppercase", "lowercase", "capital", "camelcase", "snake_case"),
            keywordsBn = list("কেস", "বড় হাত", "ছোট হাত", "ক্যাপিটাল"),
            route = "tool/case_converter",
            isPopular = true
        ),
        ToolItem(
            id = "tool_remove_spaces",
            categoryId = CategoryId.TEXT_TOOLS,
            titleEn = "Remove Extra Spaces",
            titleBn = "অতিরিক্ত স্পেস মোছা",
            descriptionEn = "Clean text by removing redundant whitespace, tabs, and empty lines.",
            descriptionBn = "অপ্রয়োজনীয় ফাঁকা জায়গা এবং ফাঁকা লাইন দ্রুত সরান।",
            keywordsEn = list("spaces", "clean", "trim", "whitespace", "format", "sanitize"),
            keywordsBn = list("স্পেস", "ফাঁকা", "পরিষ্কার", "লাইন"),
            route = "tool/remove_spaces"
        ),
        ToolItem(
            id = "tool_word_counter",
            categoryId = CategoryId.TEXT_TOOLS,
            titleEn = "Word Counter",
            titleBn = "শব্দ গণনাকারী",
            descriptionEn = "Count words, characters, sentences, paragraphs, and reading time.",
            descriptionBn = "শব্দ, বর্ণ, বাক্য, অনুচ্ছেদ এবং পড়ার সময় গণনা।",
            keywordsEn = list("word", "counter", "characters", "length", "reading time", "letters"),
            keywordsBn = list("শব্দ", "গণনা", "অক্ষর", "বাক্য", "পড়ার সময়"),
            route = "tool/word_counter",
            isPopular = true
        ),
        ToolItem(
            id = "tool_font_changer",
            categoryId = CategoryId.TEXT_TOOLS,
            titleEn = "Font Changer",
            titleBn = "ফন্ট স্টাইলার",
            descriptionEn = "Generate stylized Unicode fonts (Bold, Gothic, Script, Monospace).",
            descriptionBn = "স্টাইলিশ ইউনিকোড ফন্টে সাধারণ টেক্সট পরিবর্তন করুন।",
            keywordsEn = list("font", "changer", "style", "unicode", "fancy", "gothic", "script"),
            keywordsBn = list("ফন্ট", "স্টাইল", "ইউনিকোড", "সুন্দর লেখা"),
            route = "tool/font_changer"
        ),

        // CALCULATORS
        ToolItem(
            id = "tool_calculator",
            categoryId = CategoryId.CALCULATORS,
            titleEn = "Standard & Scientific",
            titleBn = "ক্যালকুলেটর",
            descriptionEn = "Simple and advanced scientific calculator with history tape.",
            descriptionBn = "সাধারণ ও বৈজ্ঞানিক গাণিতিক সমাধান এবং হিস্টোরি।",
            keywordsEn = list("calculator", "math", "scientific", "addition", "calculate", "history"),
            keywordsBn = list("ক্যালকুলেটর", "গণিত", "হিসাব", "যোগ", "ভাগ"),
            route = "tool/calculator",
            isPopular = true
        ),
        ToolItem(
            id = "tool_age_calculator",
            categoryId = CategoryId.CALCULATORS,
            titleEn = "Age Calculator",
            titleBn = "বয়স ও দিনপঞ্জি ক্যালকুলেটর",
            descriptionEn = "Calculate exact age, lifetime days, next birthday, Bangla and Hijri dates.",
            descriptionBn = "সঠিক বয়স, মোট দিন, পরবর্তী জন্মদিন, বাংলা সন ও হিজরি তারিখ।",
            keywordsEn = list("age", "calculator", "birthday", "bangla", "hijri", "dob", "days"),
            keywordsBn = list("বয়স", "জন্মদিন", "ক্যালেন্ডার", "বাংলা সন", "হিজরি"),
            route = "tool/age_calculator",
            isPopular = true,
            isNew = true
        ),
        ToolItem(
            id = "tool_percentage_calculator",
            categoryId = CategoryId.CALCULATORS,
            titleEn = "Percentage Calculator",
            titleBn = "শতকরা ও ডিসকাউন্ট",
            descriptionEn = "Calculate percentages, percentage change, and discounts with live savings.",
            descriptionBn = "শতাংশ, শতকরা বৃদ্ধি-হ্রাস এবং কেনাকাটার ডিসকাউন্ট হিসাব।",
            keywordsEn = list("percent", "percentage", "discount", "increase", "decrease", "math"),
            keywordsBn = list("শতকরা", "শতাংশ", "ডিসকাউন্ট", "ছাড়", "লাভ"),
            route = "tool/percentage_calculator",
            isPopular = true,
            isNew = true
        ),
        ToolItem(
            id = "tool_currency_converter",
            categoryId = CategoryId.CALCULATORS,
            titleEn = "Currency Converter",
            titleBn = "মুদ্রা রূপান্তর (কারেন্সি)",
            descriptionEn = "Convert between 18+ currencies (BDT, USD, EUR, SAR, AED, INR) with live & cached rates.",
            descriptionBn = "টাকা, ডলার, রিয়াল, ইউরো ইত্যাদি মুদ্রার লাইভ ও অফলাইন রূপান্তর।",
            keywordsEn = list("currency", "converter", "bdt", "usd", "taka", "dollar", "riyal", "exchange rate"),
            keywordsBn = list("কারেন্সি", "টাকা", "ডলার", "মুদ্রা", "রিয়াল", "ইউরো"),
            route = "tool/currency_converter",
            isPopular = true,
            isNew = true
        ),
        ToolItem(
            id = "tool_unit_converter",
            categoryId = CategoryId.CALCULATORS,
            titleEn = "Unit Converter",
            titleBn = "ইউনিট কনভার্টার",
            descriptionEn = "Convert length, mass, temperature, speed, area, and volume accurately.",
            descriptionBn = "দৈর্ঘ্য, ওজন, তাপমাত্রা, গতি ও ক্ষেত্রফল রূপান্তর।",
            keywordsEn = list("unit", "convert", "converter", "length", "temperature", "weight", "meter", "kg"),
            keywordsBn = list("ইউনিট", "একক", "রূপান্তর", "মিটার", "ওজন", "তাপমাত্রা"),
            route = "tool/unit_converter",
            isPopular = true
        ),
        ToolItem(
            id = "tool_date_calculator",
            categoryId = CategoryId.CALCULATORS,
            titleEn = "Date & Time Calculator",
            titleBn = "তারিখ ও সময় গণনা",
            descriptionEn = "Calculate duration between dates and add or subtract calendar days.",
            descriptionBn = "দুই তারিখের ব্যবধান এবং দিন যোগ বা বিয়োগ করুন।",
            keywordsEn = list("date", "time", "calendar", "days", "duration", "age"),
            keywordsBn = list("তারিখ", "সময়", "ক্যালেন্ডার", "দিন", "বয়স"),
            route = "tool/date_calculator"
        ),
        ToolItem(
            id = "tool_stopwatch",
            categoryId = CategoryId.CALCULATORS,
            titleEn = "Stopwatch & Timer",
            titleBn = "স্টপওয়াচ ও ল্যাপ টাইমার",
            descriptionEn = "High-precision millisecond stopwatch with lap tracking.",
            descriptionBn = "ল্যাপ ট্র্যাকিং সহ মিলিসেকেন্ড নির্ভুল স্টপওয়াচ।",
            keywordsEn = list("stopwatch", "timer", "lap", "chronometer", "seconds"),
            keywordsBn = list("স্টপওয়াচ", "টাইমার", "সময়", "সেকেন্ড"),
            route = "tool/stopwatch"
        ),
        ToolItem(
            id = "tool_random_generator",
            categoryId = CategoryId.CALCULATORS,
            titleEn = "Random Generator",
            titleBn = "র‍্যান্ডম জেনারেটর",
            descriptionEn = "Pick numbers in ranges, flip coins, roll dice, or draw from custom lists.",
            descriptionBn = "লটারি, কয়েন ফ্লিপ, ছক্কার চাল বা র‍্যান্ডম সংখ্যা তৈরি।",
            keywordsEn = list("random", "dice", "coin", "flip", "number", "picker", "draw"),
            keywordsBn = list("র‍্যান্ডম", "লটারি", "কয়েন", "ছক্কা", "সংখ্যা"),
            route = "tool/random_generator"
        ),

        // PHONE MANAGER
        ToolItem(
            id = "tool_device_info",
            categoryId = CategoryId.PHONE_MANAGER,
            titleEn = "Device & System Specs",
            titleBn = "ডিভাইস ও সিস্টেম তথ্য",
            descriptionEn = "Real Android OS version, SDK level, board, display density, and uptime.",
            descriptionBn = "অ্যান্ড্রয়েড ভার্সন, হার্ডওয়্যার, ডিসপ্লে রেজোলিউশন ও আপটাইম।",
            keywordsEn = list("device", "system", "hardware", "android", "specs", "model", "screen"),
            keywordsBn = list("ডিভাইস", "সিস্টেম", "হার্ডওয়্যার", "স্ক্রিন", "মডেল"),
            route = "tool/device_info",
            isPopular = true
        ),
        ToolItem(
            id = "tool_storage_info",
            categoryId = CategoryId.PHONE_MANAGER,
            titleEn = "Storage & RAM Monitor",
            titleBn = "স্টোরেজ ও র‍্যাম মনিটর",
            descriptionEn = "Internal storage usage breakdown and live RAM memory status.",
            descriptionBn = "ইন্টারনাল মেমোরি ব্যবহার এবং লাইভ র‍্যাম স্ট্যাটাস।",
            keywordsEn = list("storage", "memory", "ram", "disk", "free space", "internal"),
            keywordsBn = list("স্টোরেজ", "র‍্যাম", "মেমোরি", "খালি জায়গা"),
            route = "tool/storage_info"
        ),
        ToolItem(
            id = "tool_battery_info",
            categoryId = CategoryId.PHONE_MANAGER,
            titleEn = "Battery Health & Status",
            titleBn = "ব্যাটারি হেলথ ও চার্জিং",
            descriptionEn = "Live battery percentage, charging state, power source, and health.",
            descriptionBn = "ব্যাটারি শতাংশ, চার্জিং অবস্থা এবং স্বাস্থ্য মনিটর।",
            keywordsEn = list("battery", "power", "charge", "charging", "health", "voltage"),
            keywordsBn = list("ব্যাটারি", "চার্জ", "চার্জার", "পাওয়ার"),
            route = "tool/battery_info"
        ),

        // WRITER TOOLS
        ToolItem(
            id = "tool_writer_editor",
            categoryId = CategoryId.WRITER_TOOLS,
            titleEn = "Writer & Document Editor",
            titleBn = "ডকুমেন্ট রাইটার ও ওয়ার্ড এডিটর",
            descriptionEn = "Professional Microsoft Word-style page editor with rich formatting, tables, image insertion, PDF export, and print.",
            descriptionBn = "মাইক্রোসফট ওয়ার্ড সদৃশ পেজ এডিটর, রিচ ফরম্যাটিং, টেবিল, ছবি, পিডিএফ ও প্রিন্ট সুবিধা।",
            keywordsEn = list("writer", "document", "word", "editor", "pdf", "a4", "letter", "table", "print", "formatting"),
            keywordsBn = list("ডকুমেন্ট", "রাইটার", "ওয়ার্ড", "এডিটর", "পিডিএফ", "প্রিন্ট", "টেবিল"),
            route = "tool/writer_editor",
            isPopular = true,
            isNew = true
        ),

        // IMAGE TOOLS
        ToolItem(
            id = "tool_image_tools",
            categoryId = CategoryId.IMAGE_TOOLS,
            titleEn = "Image Inspector & Palette",
            titleBn = "ছবি পরিমাপক ও কালার প্যালেট",
            descriptionEn = "Analyze image resolution, aspect ratio, rotate, and sample dominant color hexes.",
            descriptionBn = "ছবির সাইজ, অ্যাসপেক্ট রেশিও, রোটেশন এবং কালার হেক্স সংগ্রহ।",
            keywordsEn = list("image", "photo", "palette", "colors", "rotate", "dimensions", "picture"),
            keywordsBn = list("ছবি", "ফটো", "রং", "প্যালেট", "রোটেশন"),
            route = "tool/image_tools",
            isNew = true
        ),

        // PDF TOOLS
        ToolItem(
            id = "tool_text_to_pdf",
            categoryId = CategoryId.PDF_TOOLS,
            titleEn = "Text to PDF Generator",
            titleBn = "টেক্সট থেকে পিডিএফ",
            descriptionEn = "Generate real Android PDF documents from formatted text with share sheet.",
            descriptionBn = "যেকোনো টেক্সট থেকে স্ট্যান্ডার্ড পিডিএফ ফাইল তৈরি করুন।",
            keywordsEn = list("pdf", "document", "generate", "create", "export", "text to pdf"),
            keywordsBn = list("পিডিএফ", "ডকুমেন্ট", "ফাইল", "তৈরি"),
            route = "tool/text_to_pdf",
            isPopular = true,
            isNew = true
        ),

        // QR TOOLS
        ToolItem(
            id = "tool_qr_generator",
            categoryId = CategoryId.QR_TOOLS,
            titleEn = "QR Code Generator",
            titleBn = "কিউআর কোড জেনারেটর",
            descriptionEn = "Generate instant QR code graphics for Text, URLs, WiFi, and Phone numbers.",
            descriptionBn = "টেক্সট, লিংক, ওয়াইফাই ও ফোনের জন্য উচ্চমানের কিউআর তৈরি।",
            keywordsEn = list("qr", "barcode", "generate", "code", "scanner", "wifi", "link", "url"),
            keywordsBn = list("কিউআর", "কোড", "বারকোড", "লিংক"),
            route = "tool/qr_generator",
            isPopular = true
        ),
        ToolItem(
            id = "tool_qr_scanner",
            categoryId = CategoryId.QR_TOOLS,
            titleEn = "QR & Barcode Scanner",
            titleBn = "কিউআর ও বারকোড স্ক্যানার",
            descriptionEn = "Scan QR codes and barcodes with live camera, flashlight, and gallery image support.",
            descriptionBn = "ক্যামেরা ও গ্যালারির ছবি থেকে যেকোনো কিউআর ও বারকোড স্ক্যান করুন।",
            keywordsEn = list("scan", "scanner", "qr", "barcode", "camera", "read", "decode"),
            keywordsBn = list("স্ক্যান", "স্ক্যানার", "কিউআর", "বারকোড", "ক্যামেরা"),
            route = "tool/qr_scanner",
            isPopular = true,
            isNew = true
        ),

        // VIDEO TOOLS
        ToolItem(
            id = "tool_video_tools",
            categoryId = CategoryId.VIDEO_TOOLS,
            titleEn = "Aspect Ratio & Bitrate Calc",
            titleBn = "অ্যাসপেক্ট রেশিও ও বিটরেট",
            descriptionEn = "Calculate video aspect ratios (16:9, 9:16, 4:3) and estimate target bitrates.",
            descriptionBn = "ভিডিওর অনুপাত এবং মেগাবাইট সাইজ হিসাব করুন।",
            keywordsEn = list("video", "aspect ratio", "16:9", "bitrate", "resolution", "fps"),
            keywordsBn = list("ভিডিও", "অ্যাসপেক্ট রেশিও", "রেজোলিউশন"),
            route = "tool/video_tools",
            isNew = true
        )
    )

    private fun list(vararg elements: String): List<String> = elements.toList()

    fun findToolById(id: String): ToolItem? = tools.find { it.id == id }

    fun getCategory(id: CategoryId): ToolCategory = categories.first { it.id == id }

    fun getToolsForCategory(categoryId: CategoryId): List<ToolItem> {
        return tools.filter { it.categoryId == categoryId }
    }

    fun searchTools(query: String): List<ToolItem> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return tools.filter { tool ->
            tool.titleEn.lowercase().contains(q) ||
            tool.titleBn.contains(q) ||
            tool.descriptionEn.lowercase().contains(q) ||
            tool.descriptionBn.contains(q) ||
            tool.keywordsEn.any { it.contains(q) } ||
            tool.keywordsBn.any { it.contains(q) }
        }
    }
}
