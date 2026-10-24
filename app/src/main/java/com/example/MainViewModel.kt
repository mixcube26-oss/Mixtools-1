package com.example

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PreferencesRepository
import com.example.data.ToolRegistry
import com.example.data.ToolRepository
import com.example.data.db.DraftNoteEntity
import com.example.data.db.MixToolsDatabase
import com.example.data.db.RecentToolEntity
import com.example.model.AppLanguage
import com.example.model.CategoryId
import com.example.model.ThemeMode
import com.example.model.ToolCategory
import com.example.model.ToolItem
import com.example.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MixToolsDatabase.getInstance(application)
    private val preferencesRepository = PreferencesRepository(application)
    private val toolRepository = ToolRepository(database)
    val currencyRepository = com.example.data.currency.CurrencyRepository(database, application)
    val writerRepository = com.example.data.writer.WriterRepository(database)
    val authManager = com.example.data.auth.AuthManager(application)
    val telemetryManager = com.example.data.telemetry.DeviceTelemetryManager(application)
    val appUpdateManager = com.example.data.update.AppUpdateManager(application)

    val currentUser = authManager.currentUser
    val authState = authManager.authState

    val appUpdate: StateFlow<com.example.data.update.AppUpdatePayload?> = appUpdateManager.observeAppUpdates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isUpdateDismissed = MutableStateFlow(false)
    val isUpdateDismissed: StateFlow<Boolean> = _isUpdateDismissed.asStateFlow()

    fun dismissUpdate() {
        _isUpdateDismissed.value = true
    }

    init {
        viewModelScope.launch {
            telemetryManager.registerDeviceTelemetry()
        }
    }

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferences

    val favoriteIds: StateFlow<List<String>> = toolRepository.getFavoriteToolIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTools: StateFlow<List<RecentToolEntity>> = toolRepository.getRecentTools()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val draftNotes: StateFlow<List<DraftNoteEntity>> = database.draftNoteDao().getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedWriterDocuments: StateFlow<List<com.example.data.db.WriterDocumentEntity>> = writerRepository.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<CategoryId?>(null)
    val selectedCategoryFilter: StateFlow<CategoryId?> = _selectedCategoryFilter.asStateFlow()

    val searchResults: StateFlow<List<ToolItem>> = combine(
        _searchQuery,
        _selectedCategoryFilter
    ) { query, categoryFilter ->
        val results = if (query.isBlank()) {
            ToolRegistry.tools
        } else {
            ToolRegistry.searchTools(query)
        }
        if (categoryFilter != null) {
            results.filter { it.categoryId == categoryFilter }
        } else {
            results
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ToolRegistry.tools)

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun setCategoryFilter(categoryId: CategoryId?) {
        _selectedCategoryFilter.value = categoryId
    }

    fun toggleFavorite(toolId: String) {
        val currentIsFav = favoriteIds.value.contains(toolId)
        viewModelScope.launch {
            toolRepository.toggleFavorite(toolId, currentIsFav)
            triggerHaptic()
        }
    }

    fun recordToolLaunch(toolId: String) {
        viewModelScope.launch {
            toolRepository.recordToolUsage(toolId)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            toolRepository.clearHistory()
            triggerHaptic()
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        preferencesRepository.setThemeMode(mode)
        triggerHaptic()
    }

    fun setLanguage(lang: AppLanguage) {
        preferencesRepository.setLanguage(lang)
        triggerHaptic()
    }

    fun setHapticFeedback(enabled: Boolean) {
        preferencesRepository.setHapticFeedback(enabled)
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        preferencesRepository.setAnimationsEnabled(enabled)
    }

    fun saveDraftNote(title: String, content: String, fontStyle: String, fontSize: Int, textColor: Long) {
        viewModelScope.launch {
            database.draftNoteDao().saveNote(
                DraftNoteEntity(
                    title = title.ifBlank { "Untitled Note" },
                    content = content,
                    fontStyle = fontStyle,
                    fontSize = fontSize,
                    textColor = textColor
                )
            )
            triggerHaptic()
        }
    }

    fun deleteDraftNote(id: Long) {
        viewModelScope.launch {
            database.draftNoteDao().deleteNote(id)
            triggerHaptic()
        }
    }

    fun saveWriterDocument(doc: com.example.data.db.WriterDocumentEntity, onComplete: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            val id = writerRepository.saveDocument(doc)
            onComplete?.invoke(id)
            triggerHaptic()
        }
    }

    fun deleteWriterDocument(id: Long) {
        viewModelScope.launch {
            writerRepository.deleteDocument(id)
            triggerHaptic()
        }
    }

    fun signInWithGoogle(activity: android.app.Activity, onComplete: ((Result<com.google.firebase.auth.FirebaseUser>) -> Unit)? = null) {
        viewModelScope.launch {
            val result = authManager.signInWithGoogle(activity)
            onComplete?.invoke(result)
            triggerHaptic()
        }
    }

    fun signOut(activity: android.app.Activity? = null, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            authManager.signOut(activity)
            onComplete?.invoke()
            triggerHaptic()
        }
    }

    fun clearAuthError() {
        authManager.clearError()
    }

    fun saveCustomWebClientId(clientId: String) {
        authManager.saveCustomWebClientId(clientId)
    }

    fun getWebClientId(): String? {
        return authManager.getWebClientId()
    }

    private fun triggerHaptic() {
        if (!userPreferences.value.hapticFeedback) return
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
        } catch (_: Exception) {}
    }
}
