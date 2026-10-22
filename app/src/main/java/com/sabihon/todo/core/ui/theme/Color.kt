package com.sabihon.todo.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Notely-Inspired Dark Theme
 * Dark-first design with vibrant accent layers
 * Following Notely's design philosophy: clarity through contrast
 */

// ==================== NOTELY COLOR PALETTE ====================

// Dark backgrounds (primary)
val NotelyBackground = Color(0xFF1A1A1A)        // Deep charcoal - main background
val NotelyBackgroundAlt = Color(0xFF121212)     // Darker variant for contrast

// Surfaces
val NotelySurface = Color(0xFF252525)           // Slightly lighter than background
val NotelySurfaceVariant = Color(0xFF2F2F2F)    // For tertiary containers
val NotelySurfaceHigh = Color(0xFF3A3A3A)       // For elevated surfaces

// Accent Colors (Notely signature)
val NotelyAccentPrimary = Color(0xFF5B7AFF)     // Vibrant blue - main CTAs, selection
val NotelyAccentSecondary = Color(0xFFFF8C42)   // Warm orange - warnings, suggestions
val NotelyAccentError = Color(0xFFE74C3C)       // Red - destructive actions
val NotelyAccentSuccess = Color(0xFF4CAF50)     // Green - completed state

// Text colors
val NotelyTextPrimary = Color(0xFFFFFFFF)       // Pure white for main text
val NotelyTextSecondary = Color(0xFFB0B0B0)     // Light gray for secondary text
val NotelyTextTertiary = Color(0xFF666666)      // Muted gray for hints

// Status colors (accent badges)
val StatusInProgress = Color(0xFF9C27B0)        // Purple
val StatusCompleted = Color(0xFF4CAF50)         // Green
val StatusOverdue = Color(0xFFE74C3C)           // Red
val StatusToday = Color(0xFFFF8C42)             // Orange

// Legacy colors (kept for compatibility)
val SoftBlue = Color(0xFFD6E4FF)
val SoftYellow = Color(0xFFFDF3A0)
val SoftMint = Color(0xFFCFF5E7)
val SoftPink = Color(0xFFFBD7EA)
val SoftLilac = Color(0xFFE5DBFF)
val DeepGreen = Color(0xFF2F6B4F)
val AccentBlue = Color(0xFF2F6BFF)

val BgLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF5F6FA)
val TextPrimaryLight = Color(0xFF101114)
val TextSecondaryLight = Color(0xFF6B7280)

// Dark adjustments
val SoftBlueDark = Color(0xFF2D3D5E)
val SoftYellowDark = Color(0xFF5A5428)
val SoftMintDark = Color(0xFF244A3C)
val SoftPinkDark = Color(0xFF5A3A4A)
val SoftLilacDark = Color(0xFF3D2E5E)
val DeepGreenDark = Color(0xFF1E4A35)
val BgDark = NotelyBackground
val SurfaceVariantDark = NotelySurfaceVariant
val TextPrimaryDark = NotelyTextPrimary
val TextSecondaryDark = NotelyTextSecondary

// Priority colors
val PriorityLow = Color(0xFF8BC48A)
val PriorityMedium = Color(0xFFF2C94C)
val PriorityHigh = Color(0xFFF2994A)
val PriorityUrgent = Color(0xFFEB5757)

// Priority background tints
val PriorityLowBg = Color(0xFFE8F5E9)
val PriorityMediumBg = Color(0xFFFFF8E1)
val PriorityHighBg = Color(0xFFFFF3E0)
val PriorityUrgentBg = Color(0xFFFFEBEE)

// Category palette list for reuse
val CategoryPalette = listOf(
    NotelyAccentPrimary, NotelyAccentSecondary, NotelyAccentSuccess,
    StatusInProgress, StatusToday, StatusOverdue
)

val CategoryPaletteHex = listOf(
    "#5B7AFF", "#FF8C42", "#4CAF50", "#9C27B0", "#FF8C42", "#E74C3C"
)

// Dividers and borders
val DividerLight = Color(0xFFE5E7EB)
val DividerDark = Color(0xFF404040)             // Subtle divider for dark theme
val Scrim = Color(0x80000000)

/**
 * Helper to parse hex color string.
 */
fun parseColorHex(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        NotelyAccentPrimary
    }
}
