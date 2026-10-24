package com.example.model

enum class CategoryId {
    PHONE_MANAGER,
    CALCULATORS,
    WRITER_TOOLS,
    IMAGE_TOOLS,
    PDF_TOOLS,
    QR_TOOLS,
    VIDEO_TOOLS,
    TEXT_TOOLS
}

data class ToolCategory(
    val id: CategoryId,
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val colorHex: Long,
    val iconName: String
)

data class ToolItem(
    val id: String,
    val categoryId: CategoryId,
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val keywordsEn: List<String>,
    val keywordsBn: List<String>,
    val route: String,
    val isPopular: Boolean = false,
    val isNew: Boolean = false
)

enum class ThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

enum class AppLanguage {
    ENGLISH,
    BANGLA
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val hapticFeedback: Boolean = true,
    val animationsEnabled: Boolean = true
)
