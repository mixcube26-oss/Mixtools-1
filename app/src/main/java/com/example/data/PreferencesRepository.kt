package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppLanguage
import com.example.model.ThemeMode
import com.example.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("mixtools_prefs", Context.MODE_PRIVATE)

    private val _userPreferences = MutableStateFlow(loadPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        val themeStr = prefs.getString("theme_mode", ThemeMode.DARK.name) ?: ThemeMode.DARK.name
        val theme = runCatching { ThemeMode.valueOf(themeStr) }.getOrDefault(ThemeMode.DARK)

        val langStr = prefs.getString("language", AppLanguage.ENGLISH.name) ?: AppLanguage.ENGLISH.name
        val lang = runCatching { AppLanguage.valueOf(langStr) }.getOrDefault(AppLanguage.ENGLISH)

        val haptics = prefs.getBoolean("haptic_feedback", true)
        val animations = prefs.getBoolean("animations_enabled", true)

        return UserPreferences(
            themeMode = theme,
            language = lang,
            hapticFeedback = haptics,
            animationsEnabled = animations
        )
    }

    fun setThemeMode(themeMode: ThemeMode) {
        prefs.edit().putString("theme_mode", themeMode.name).apply()
        _userPreferences.value = _userPreferences.value.copy(themeMode = themeMode)
    }

    fun setLanguage(language: AppLanguage) {
        prefs.edit().putString("language", language.name).apply()
        _userPreferences.value = _userPreferences.value.copy(language = language)
    }

    fun setHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean("haptic_feedback", enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(hapticFeedback = enabled)
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("animations_enabled", enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(animationsEnabled = enabled)
    }
}
