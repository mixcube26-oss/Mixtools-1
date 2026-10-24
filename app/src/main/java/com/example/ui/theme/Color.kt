package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark Theme Colors (Mockup Deep Navy / Space Dark)
val DeepNavyDark = Color(0xFF070B19)
val SurfaceDark = Color(0xFF0D1630)
val SurfaceVariantDark = Color(0xFF142044)
val SurfaceContainerDark = Color(0xFF1A2A5A)
val OutlineDark = Color(0xFF22366A)
val OutlineBright = Color(0xFF385299)

// Accent Colors
val AccentCyan = Color(0xFF00D2FF)
val AccentBlue = Color(0xFF3B82F6)
val AccentPurple = Color(0xFFA855F7)
val AccentMagenta = Color(0xFFEC4899)
val AccentOrange = Color(0xFFF97316)
val AccentAmber = Color(0xFFF59E0B)
val AccentGreen = Color(0xFF10B981)

// Light Theme Colors
val BackgroundLight = Color(0xFFF8FAFC)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF1F5F9)
val OutlineLight = Color(0xFFCBD5E1)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF64748B)

// Gradients
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF00D2FF), Color(0xFF7F00FF), Color(0xFFFF007F))
)

val HeroBannerGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF1E3A8A), Color(0xFF4C1D95), Color(0xFF6B21A8))
)

val ProBadgeGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
)

val CardDarkGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF131D3F), Color(0xFF0D152F))
)
