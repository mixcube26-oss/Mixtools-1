package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.auth.AuthState
import com.example.model.AppLanguage
import com.example.model.ThemeMode
import com.example.model.UserPreferences
import com.example.ui.components.AboutMixToolsDialog
import com.example.ui.components.DeveloperAttributionCard
import com.example.ui.components.OutlinedStateButton
import com.example.ui.theme.ProBadgeGradient
import com.google.firebase.auth.FirebaseUser

@Composable
fun SettingsScreen(
    userPreferences: UserPreferences,
    currentUser: FirebaseUser? = null,
    authState: AuthState = AuthState.Idle,
    currentWebClientId: String? = null,
    onSignInWithGoogle: (Activity) -> Unit = {},
    onSignOut: (Activity?) -> Unit = {},
    onSaveCustomWebClientId: (String) -> Unit = {},
    onClearAuthError: () -> Unit = {},
    onThemeModeChange: (ThemeMode) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onHapticFeedbackChange: (Boolean) -> Unit,
    onAnimationsChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val language = userPreferences.language

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showProDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity
    var showSignOutConfirmDialog by remember { mutableStateOf(false) }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = if (language == AppLanguage.BANGLA) "ভাষা নির্বাচন করুন" else "Select Language",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguageChange(AppLanguage.ENGLISH)
                                showLanguageDialog = false
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = userPreferences.language == AppLanguage.ENGLISH,
                            onClick = {
                                onLanguageChange(AppLanguage.ENGLISH)
                                showLanguageDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("English", fontSize = 16.sp)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguageChange(AppLanguage.BANGLA)
                                showLanguageDialog = false
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = userPreferences.language == AppLanguage.BANGLA,
                            onClick = {
                                onLanguageChange(AppLanguage.BANGLA)
                                showLanguageDialog = false
                            }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("বাংলা (Bangla)", fontSize = 16.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(if (language == AppLanguage.BANGLA) "বন্ধ করুন" else "Close")
                }
            }
        )
    }

    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text = if (language == AppLanguage.BANGLA) "থিম নির্বাচন" else "Theme Appearance",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    listOf(
                        Triple(ThemeMode.DARK, "Dark Mode", "ডার্ক মোড"),
                        Triple(ThemeMode.LIGHT, "Light Mode", "লাইট মোড"),
                        Triple(ThemeMode.SYSTEM, "System Default", "সিস্টেম ডিফল্ট")
                    ).forEach { (mode, enText, bnText) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onThemeModeChange(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = userPreferences.themeMode == mode,
                                onClick = {
                                    onThemeModeChange(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(if (language == AppLanguage.BANGLA) bnText else enText, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(if (language == AppLanguage.BANGLA) "ঠিক আছে" else "OK")
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AboutMixToolsDialog(
            language = language,
            onDismiss = { showAboutDialog = false }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    text = if (language == AppLanguage.BANGLA) "গোপনীয়তা নীতি" else "Privacy & Data Policy",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        if (language == AppLanguage.BANGLA)
                            "• আপনার সমস্ত ডেটা ডিভাইসের অভ্যন্তরীণ মেমোরিতে নিরাপদে সংরক্ষিত থাকে।\n• কোনো ব্যক্তিগত তথ্য তৃতীয় পক্ষের সাথে শেয়ার করা হয় না।\n• ক্যামেরা বা কন্টাক্টস এর অপ্রয়োজনীয় পারমিশন চাওয়া হয় না।"
                        else
                            "• All utility computations, draft notes, and history are kept locally on your device.\n• Zero unnecessary platform permissions.\n• 100% offline-first utility operations with future opt-in cloud sync."
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(if (language == AppLanguage.BANGLA) "সম্মতি" else "Understood")
                }
            }
        )
    }

    // Feedback Dialog
    if (showFeedbackDialog) {
        var feedbackText by remember { mutableStateOf("") }
        var submitted by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showFeedbackDialog = false },
            title = {
                Text(
                    text = if (language == AppLanguage.BANGLA) "মতামত বা ফিডব্যাক" else "Send Feedback",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (submitted) {
                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "ধন্যবাদ! আপনার মতামত সফলভাবে গৃহীত হয়েছে।"
                        else
                            "Thank you! Your feedback has been noted for our product roadmap."
                    )
                } else {
                    Column {
                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "মিক্সটুলস আরও উন্নত করতে আপনার মূল্যবান মতামত জানান:"
                            else
                                "Help us improve MixTools with your suggestions or feature requests:"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            placeholder = { Text("Your comments...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4
                        )
                    }
                }
            },
            confirmButton = {
                if (submitted) {
                    TextButton(onClick = { showFeedbackDialog = false }) {
                        Text(if (language == AppLanguage.BANGLA) "বন্ধ করুন" else "Close")
                    }
                } else {
                    TextButton(
                        onClick = {
                            if (feedbackText.isNotBlank()) {
                                submitted = true
                            }
                        }
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "জমা দিন" else "Submit",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            dismissButton = {
                if (!submitted) {
                    TextButton(onClick = { showFeedbackDialog = false }) {
                        Text(if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel")
                    }
                }
            }
        )
    }

    // PRO Dialog
    if (showProDialog) {
        AlertDialog(
            onDismissRequest = { showProDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("MixTools PRO", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    if (language == AppLanguage.BANGLA)
                        "প্রো সুবিধা:\n• আনলিমিটেড ডকুমেন্ট এক্সপোর্ট\n• প্রিমিয়াম ফন্ট লাইব্রেরি\n• ক্লাউড ব্যাকআপ সিংক (আসন্ন সংস্করণ)\n• বিজ্ঞাপনমুক্ত অভিজ্ঞতা"
                    else
                        "PRO Benefits:\n• Unlimited rich-text document exports\n• Advanced batch utilities\n• Cloud backup & sync (Upcoming Phase)\n• 100% ad-free experience"
                )
            },
            confirmButton = {
                TextButton(onClick = { showProDialog = false }) {
                    Text(if (language == AppLanguage.BANGLA) "ঠিক আছে" else "Got It")
                }
            }
        )
    }

    // Sign Out Confirmation Dialog
    if (showSignOutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirmDialog = false },
            title = {
                Text(
                    text = if (language == AppLanguage.BANGLA) "সাইন আউট নিশ্চিত করুন" else "Confirm Sign Out",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == AppLanguage.BANGLA)
                        "আপনি কি নিশ্চিত যে আপনার Google অ্যাকাউন্ট থেকে সাইন আউট করতে চান?"
                    else
                        "Are you sure you want to sign out from your Google account?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutConfirmDialog = false
                        onSignOut(activity)
                    }
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "সাইন আউট" else "Sign Out",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirmDialog = false }) {
                    Text(if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel")
                }
            }
        )
    }

    // Auth Error Alert Dialog
    if (authState is AuthState.Error) {
        AlertDialog(
            onDismissRequest = { onClearAuthError() },
            title = {
                Text(
                    text = if (language == AppLanguage.BANGLA) "সাইন ইন বার্তা" else "Sign In Notice",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(text = authState.message)
            },
            confirmButton = {
                TextButton(onClick = { onClearAuthError() }) {
                    Text(if (language == AppLanguage.BANGLA) "ঠিক আছে" else "OK")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen_lazy_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile & Optional Google Authentication Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("user_profile_card"),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (currentUser != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val photoUrl = currentUser?.photoUrl
                            val initial = currentUser?.displayName?.firstOrNull()?.uppercase()
                            if (photoUrl != null) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "User Photo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else if (initial != null) {
                                Text(
                                    text = initial,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            if (currentUser != null) {
                                Text(
                                    text = currentUser.displayName?.ifBlank { "Google User" } ?: "Google User",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentUser.email ?: "Google Account",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "Google অ্যাকাউন্ট সংযুক্ত" else "Google Account Connected",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF10B981),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            } else {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "অতিথি মোড (লগইন ঐচ্ছিক)" else "Guest Mode (Sign-in Optional)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "সব টুলস অফলাইনে ব্যবহারযোগ্য" else "All utilities available offline",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons in Card
                    if (currentUser != null) {
                        OutlinedButton(
                            onClick = { showSignOutConfirmDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("sign_out_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "Google অ্যাকাউন্ট থেকে সাইন আউট" else "Sign Out from Google",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        val isLoading = authState is AuthState.Loading
                        Button(
                            onClick = {
                                if (activity != null) {
                                    onSignInWithGoogle(activity)
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("google_sign_in_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(if (language == AppLanguage.BANGLA) "সাইন ইন হচ্ছে..." else "Signing in...")
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "Google দিয়ে সাইন ইন করুন" else "Sign in with Google",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Appearance & General Settings
        item {
            Text(
                text = if (language == AppLanguage.BANGLA) "সাধারণ সেটিংস" else "General Settings",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(start = 4.dp, top = 6.dp)
            )
        }

        // Dark Mode Toggle
        item {
            SettingsItemCard(
                icon = Icons.Default.DarkMode,
                title = if (language == AppLanguage.BANGLA) "ডার্ক মোড" else "Dark Mode",
                subtitle = when (userPreferences.themeMode) {
                    ThemeMode.DARK -> if (language == AppLanguage.BANGLA) "চালু রয়েছে" else "Enabled"
                    ThemeMode.LIGHT -> if (language == AppLanguage.BANGLA) "বন্ধ রয়েছে" else "Disabled"
                    ThemeMode.SYSTEM -> if (language == AppLanguage.BANGLA) "সিস্টেম অনুসারে" else "System Followed"
                },
                onClick = { showThemeDialog = true },
                trailing = {
                    Switch(
                        checked = userPreferences.themeMode == ThemeMode.DARK,
                        onCheckedChange = { checked ->
                            onThemeModeChange(if (checked) ThemeMode.DARK else ThemeMode.LIGHT)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            )
        }

        // Language Picker
        item {
            SettingsItemCard(
                icon = Icons.Default.Language,
                title = if (language == AppLanguage.BANGLA) "ভাষা (Language)" else "Language",
                subtitle = if (userPreferences.language == AppLanguage.BANGLA) "বাংলা" else "English",
                onClick = { showLanguageDialog = true }
            )
        }

        // Section: Interaction Preferences
        item {
            Text(
                text = if (language == AppLanguage.BANGLA) "ইন্টারঅ্যাকশন সেটিংস" else "Preferences & Motion",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(start = 4.dp, top = 6.dp)
            )
        }

        // Haptic Feedback
        item {
            SettingsItemCard(
                icon = Icons.Default.Vibration,
                title = if (language == AppLanguage.BANGLA) "হ্যাপটিক ভাইব্রেশন" else "Haptic Feedback",
                subtitle = if (userPreferences.hapticFeedback)
                    (if (language == AppLanguage.BANGLA) "বাটনে স্পর্শ অনুভূতি সক্রিয়" else "Tactile vibration enabled")
                else
                    (if (language == AppLanguage.BANGLA) "নিষ্ক্রিয়" else "Disabled"),
                onClick = { onHapticFeedbackChange(!userPreferences.hapticFeedback) },
                trailing = {
                    Switch(
                        checked = userPreferences.hapticFeedback,
                        onCheckedChange = onHapticFeedbackChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            )
        }

        // Animation Toggle
        item {
            SettingsItemCard(
                icon = Icons.Default.Animation,
                title = if (language == AppLanguage.BANGLA) "স্ক্রিন অ্যানিমেশন" else "Smooth Animations",
                subtitle = if (userPreferences.animationsEnabled)
                    (if (language == AppLanguage.BANGLA) "মসৃণ ট্রানজিশন সক্রিয়" else "Smooth screen transitions enabled")
                else
                    (if (language == AppLanguage.BANGLA) "কমানো হয়েছে (Reduced Motion)" else "Reduced Motion"),
                onClick = { onAnimationsChange(!userPreferences.animationsEnabled) },
                trailing = {
                    Switch(
                        checked = userPreferences.animationsEnabled,
                        onCheckedChange = onAnimationsChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            )
        }

        // Section: About & Support
        item {
            Text(
                text = if (language == AppLanguage.BANGLA) "তথ্য ও সহায়তা" else "About & Support",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(start = 4.dp, top = 6.dp)
            )
        }

        // Privacy Policy
        item {
            SettingsItemCard(
                icon = Icons.Default.Lock,
                title = if (language == AppLanguage.BANGLA) "গোপনীয়তা ও সুরক্ষা" else "Privacy & Security",
                subtitle = if (language == AppLanguage.BANGLA) "আপনার ডেটা নিরাপদে সংরক্ষণ" else "Manage local data security",
                onClick = { showPrivacyDialog = true }
            )
        }

        // About
        item {
            SettingsItemCard(
                iconPainter = painterResource(id = R.drawable.ic_mixtools_logo),
                title = if (language == AppLanguage.BANGLA) "মিক্সটুলস সম্পর্কে" else "About MixTools",
                subtitle = "Version ${com.example.BuildConfig.VERSION_NAME}",
                onClick = { showAboutDialog = true }
            )
        }

        // Feedback
        item {
            SettingsItemCard(
                icon = Icons.Default.Feedback,
                title = if (language == AppLanguage.BANGLA) "মতামত দিন" else "Feedback & Suggestions",
                subtitle = if (language == AppLanguage.BANGLA) "আমাদের সাথে যোগাযোগ করুন" else "Share ideas to improve MixTools",
                onClick = { showFeedbackDialog = true }
            )
        }

        // Developer Attribution
        item {
            Spacer(modifier = Modifier.height(10.dp))
            DeveloperAttributionCard(
                language = language,
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://annaem143.blogspot.com/?m=1"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "ওয়েবসাইট ওপেন করা যায়নি", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SettingsItemCard(
    icon: ImageVector? = null,
    iconPainter: androidx.compose.ui.graphics.painter.Painter? = null,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("settings_item_$title"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                if (iconPainter != null) {
                    Image(
                        painter = iconPainter,
                        contentDescription = null,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (trailing != null) {
                trailing()
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}


