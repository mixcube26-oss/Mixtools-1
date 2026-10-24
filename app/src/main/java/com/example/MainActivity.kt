package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.ToolRegistry
import com.example.model.AppLanguage
import com.example.model.CategoryId
import com.example.model.ToolCategory
import com.example.model.ToolItem
import com.example.ui.components.MixBottomNav
import com.example.ui.components.MixTopBar
import com.example.ui.components.NavDestination
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CategoryDetailScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MixToolsTheme
import com.example.ui.tools.calculator.AgeCalculatorScreen
import com.example.ui.tools.calculator.CalculatorScreen
import com.example.ui.tools.calculator.ComprehensiveUnitConverterScreen
import com.example.ui.tools.calculator.CurrencyConverterScreen
import com.example.ui.tools.calculator.DateCalculatorScreen
import com.example.ui.tools.calculator.PercentageCalculatorScreen
import com.example.ui.tools.calculator.RandomGeneratorScreen
import com.example.ui.tools.calculator.StopwatchScreen
import com.example.ui.tools.calculator.UnitConverterScreen
import com.example.ui.tools.image.ImageToolsScreen
import com.example.ui.tools.pdf.TextToPdfScreen
import com.example.ui.tools.phonemanager.BatteryInfoScreen
import com.example.ui.tools.phonemanager.DeviceInfoScreen
import com.example.ui.tools.phonemanager.StorageInfoScreen
import com.example.ui.tools.qr.QrGeneratorScreen
import com.example.ui.tools.qr.QrScannerScreen
import com.example.ui.tools.text.CaseConverterScreen
import com.example.ui.tools.text.CleanSpacesScreen
import com.example.ui.tools.text.FontChangerScreen
import com.example.ui.tools.text.TextCounterScreen
import com.example.ui.tools.text.TextEditorScreen
import com.example.ui.tools.text.WordCounterScreen
import com.example.ui.tools.video.VideoToolsScreen
import com.example.ui.tools.writer.WriterEditorScreen
import com.example.ui.tools.writer.WriterFoundationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val preferences by mainViewModel.userPreferences.collectAsStateWithLifecycle()

            MixToolsTheme(themeMode = preferences.themeMode) {
                MixToolsApp(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun MixToolsApp(viewModel: MainViewModel) {
    val navController: NavHostController = rememberNavController()
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val language = preferences.language

    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val recentEntities by viewModel.recentTools.collectAsStateWithLifecycle()
    val draftNotes by viewModel.draftNotes.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val appUpdate by viewModel.appUpdate.collectAsStateWithLifecycle()
    val isUpdateDismissed by viewModel.isUpdateDismissed.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavDestination.HOME.route

    val topLevelRoutes = remember {
        listOf(
            NavDestination.HOME.route,
            NavDestination.CATEGORIES.route,
            NavDestination.FAVORITES.route,
            NavDestination.HISTORY.route,
            NavDestination.SETTINGS.route
        )
    }

    val canNavigateBack = currentRoute !in topLevelRoutes

    // Calculate title for top bar (remembered to prevent recomposition overhead)
    val screenTitle: String? = remember(currentRoute, language, navBackStackEntry) {
        when {
            currentRoute == "search" -> if (language == AppLanguage.BANGLA) "টুল অনুসন্ধান" else "Search Tools"
            currentRoute.startsWith("category/") -> {
                val catIdStr = navBackStackEntry?.arguments?.getString("categoryId")
                val catId = catIdStr?.let { runCatching { CategoryId.valueOf(it) }.getOrNull() }
                val cat = catId?.let { ToolRegistry.getCategory(it) }
                if (cat != null) {
                    if (language == AppLanguage.BANGLA) cat.titleBn else cat.titleEn
                } else null
            }
            currentRoute.startsWith("tool/") -> {
                val tool = ToolRegistry.tools.find { it.route == currentRoute }
                if (tool != null) {
                    if (language == AppLanguage.BANGLA) tool.titleBn else tool.titleEn
                } else null
            }
            else -> null
        }
    }

    fun openTool(tool: ToolItem) {
        viewModel.recordToolLaunch(tool.id)
        navController.navigate(tool.route) {
            launchSingleTop = true
        }
    }

    fun openCategory(category: ToolCategory) {
        navController.navigate("category/${category.id.name}") {
            launchSingleTop = true
        }
    }

    val showAppTopBar = currentRoute != "tool/writer_editor"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showAppTopBar) {
                MixTopBar(
                    title = screenTitle,
                    canNavigateBack = canNavigateBack,
                    currentUser = currentUser,
                    onNavigateBack = { navController.popBackStack() },
                    onMenuClick = {
                        navController.navigate(NavDestination.SETTINGS.route)
                    },
                    onProClick = {
                        navController.navigate(NavDestination.SETTINGS.route)
                    },
                    onProfileClick = {
                        navController.navigate(NavDestination.SETTINGS.route)
                    }
                )
            }
        },
        bottomBar = {
            val showBottomNav = currentRoute in topLevelRoutes || currentRoute == "search"
            if (showBottomNav) {
                val activeTabRoute = if (currentRoute in topLevelRoutes) currentRoute else NavDestination.HOME.route
                MixBottomNav(
                    currentRoute = activeTabRoute,
                    language = language,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestination.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home
            composable(NavDestination.HOME.route) {
                HomeScreen(
                    language = language,
                    favoriteIds = favoriteIds,
                    recentEntities = recentEntities,
                    onToolClick = { openTool(it) },
                    onCategoryClick = { openCategory(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onSearchClick = { navController.navigate("search") },
                    onSeeAllCategoriesClick = { navController.navigate(NavDestination.CATEGORIES.route) },
                    onSeeAllPopularClick = { navController.navigate("search") }
                )
            }

            // Categories
            composable(NavDestination.CATEGORIES.route) {
                CategoriesScreen(
                    language = language,
                    onCategoryClick = { openCategory(it) }
                )
            }

            // Category Details
            composable(
                route = "category/{categoryId}",
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val catIdStr = backStackEntry.arguments?.getString("categoryId")
                val catId = catIdStr?.let { runCatching { CategoryId.valueOf(it) }.getOrNull() } ?: CategoryId.TEXT_TOOLS
                val category = ToolRegistry.getCategory(catId)

                CategoryDetailScreen(
                    category = category,
                    language = language,
                    favoriteIds = favoriteIds,
                    onToolClick = { openTool(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) }
                )
            }

            // Favorites
            composable(NavDestination.FAVORITES.route) {
                FavoritesScreen(
                    language = language,
                    favoriteIds = favoriteIds,
                    onToolClick = { openTool(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onBrowseToolsClick = { navController.navigate(NavDestination.CATEGORIES.route) }
                )
            }

            // History
            composable(NavDestination.HISTORY.route) {
                HistoryScreen(
                    language = language,
                    recentEntities = recentEntities,
                    favoriteIds = favoriteIds,
                    onToolClick = { openTool(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onClearHistory = { viewModel.clearHistory() },
                    onBrowseToolsClick = { navController.navigate(NavDestination.HOME.route) }
                )
            }

            // Settings
            composable(NavDestination.SETTINGS.route) {
                SettingsScreen(
                    userPreferences = preferences,
                    currentUser = currentUser,
                    authState = authState,
                    currentWebClientId = viewModel.getWebClientId(),
                    onSignInWithGoogle = { act -> viewModel.signInWithGoogle(act) },
                    onSignOut = { act -> viewModel.signOut(act) },
                    onSaveCustomWebClientId = { id -> viewModel.saveCustomWebClientId(id) },
                    onClearAuthError = { viewModel.clearAuthError() },
                    onThemeModeChange = { viewModel.setThemeMode(it) },
                    onLanguageChange = { viewModel.setLanguage(it) },
                    onHapticFeedbackChange = { viewModel.setHapticFeedback(it) },
                    onAnimationsChange = { viewModel.setAnimationsEnabled(it) }
                )
            }

            // Search
            composable("search") {
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
                val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

                SearchScreen(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    selectedCategoryFilter = selectedCategoryFilter,
                    onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
                    searchResults = searchResults,
                    language = language,
                    favoriteIds = favoriteIds,
                    onToolClick = { openTool(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) }
                )
            }

            // TEXT TOOLS
            composable("tool/text_editor") {
                TextEditorScreen(
                    language = language,
                    onSaveDraft = { title, content, fontStyle, fontSize, textColor ->
                        viewModel.saveDraftNote(title, content, fontStyle, fontSize, textColor)
                    }
                )
            }
            composable("tool/text_counter") {
                TextCounterScreen(language = language)
            }
            composable("tool/case_converter") {
                CaseConverterScreen(language = language)
            }
            composable("tool/remove_spaces") {
                CleanSpacesScreen(language = language)
            }
            composable("tool/word_counter") {
                WordCounterScreen(language = language)
            }
            composable("tool/font_changer") {
                FontChangerScreen(language = language)
            }

            // CALCULATORS
            composable("tool/calculator") {
                CalculatorScreen(language = language)
            }
            composable("tool/age_calculator") {
                AgeCalculatorScreen(language = language)
            }
            composable("tool/percentage_calculator") {
                PercentageCalculatorScreen(language = language)
            }
            composable("tool/currency_converter") {
                CurrencyConverterScreen(
                    currencyRepository = viewModel.currencyRepository,
                    language = language
                )
            }
            composable("tool/unit_converter") {
                ComprehensiveUnitConverterScreen(language = language)
            }
            composable("tool/date_calculator") {
                DateCalculatorScreen(language = language)
            }
            composable("tool/stopwatch") {
                StopwatchScreen(language = language)
            }
            composable("tool/random_generator") {
                RandomGeneratorScreen(language = language)
            }

            // PHONE MANAGER
            composable("tool/device_info") {
                DeviceInfoScreen(language = language)
            }
            composable("tool/storage_info") {
                StorageInfoScreen(language = language)
            }
            composable("tool/battery_info") {
                BatteryInfoScreen(language = language)
            }

            // WRITER TOOLS
            composable("tool/writer_editor") {
                val savedDocuments by viewModel.savedWriterDocuments.collectAsStateWithLifecycle()
                WriterEditorScreen(
                    language = language,
                    themeMode = preferences.themeMode,
                    savedDocuments = savedDocuments,
                    onSaveDocument = { doc, onComplete ->
                        viewModel.saveWriterDocument(doc, onComplete)
                    },
                    onDeleteDocument = { id ->
                        viewModel.deleteWriterDocument(id)
                    },
                    onBackToApp = { navController.popBackStack() }
                )
            }

            // IMAGE TOOLS
            composable("tool/image_tools") {
                ImageToolsScreen(language = language)
            }

            // PDF TOOLS
            composable("tool/text_to_pdf") {
                TextToPdfScreen(language = language)
            }

            // QR TOOLS
            composable("tool/qr_generator") {
                QrGeneratorScreen(language = language)
            }
            composable("tool/qr_scanner") {
                QrScannerScreen(language = language)
            }

            // VIDEO TOOLS
            composable("tool/video_tools") {
                VideoToolsScreen(language = language)
            }
        }
    }

    // In-app Premium Update Dialog
    val currentUpdate = appUpdate
    if (currentUpdate != null && (currentUpdate.isForceUpdate || !isUpdateDismissed)) {
        com.example.ui.components.PremiumUpdateDialog(
            update = currentUpdate,
            onDismiss = { viewModel.dismissUpdate() }
        )
    }
}
