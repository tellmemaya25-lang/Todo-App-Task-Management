package com.sabihon.todo.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Notely-Inspired Dark Theme
 * Dark-first design with vibrant accent layers
 * Following Notely's design philosophy: clarity through contrast
 */

/**
 * Notely Dark Color Scheme - Primary theme
 * High contrast, vibrant accents, dark background
 */
private val NotelyDarkColorScheme = darkColorScheme(
    // Primary accent (blue) - main CTAs, selection
    primary = NotelyAccentPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3D4D7A),        // Darker blue for containers
    onPrimaryContainer = NotelyAccentPrimary,

    // Secondary accent (orange) - warnings, suggestions
    secondary = NotelyAccentSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF664020),      // Darker orange
    onSecondaryContainer = NotelyAccentSecondary,

    // Tertiary accent (green) - completed, success
    tertiary = NotelyAccentSuccess,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF2E6A3D),       // Darker green
    onTertiaryContainer = NotelyAccentSuccess,

    // Background and surfaces
    background = NotelyBackground,
    onBackground = NotelyTextPrimary,
    surface = NotelySurface,
    onSurface = NotelyTextPrimary,
    surfaceVariant = NotelySurfaceVariant,
    onSurfaceVariant = NotelyTextSecondary,
    surfaceContainer = Color(0xFF2F2F2F),
    surfaceContainerHigh = Color(0xFF3A3A3A),
    surfaceContainerHighest = Color(0xFF454545),

    // Outline and dividers
    outline = Color(0xFF404040),
    outlineVariant = DividerDark,
    scrim = Scrim,

    // Error colors
    error = NotelyAccentError,
    onError = Color.White,
    errorContainer = Color(0xFF8B1F1F),
    onErrorContainer = NotelyAccentError
)

/**
 * Light Color Scheme - Optional, for light mode
 * Kept for compatibility but dark theme is primary
 */
private val LightColorScheme = lightColorScheme(
    primary = NotelyAccentPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EFFF),
    onPrimaryContainer = NotelyAccentPrimary,
    secondary = NotelyAccentSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF4E0),
    onSecondaryContainer = NotelyAccentSecondary,
    tertiary = NotelyAccentSuccess,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE8F5E9),
    onTertiaryContainer = NotelyAccentSuccess,
    background = Color.White,
    onBackground = NotelyTextPrimary,
    surface = Color.White,
    onSurface = NotelyTextPrimary,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = NotelyTextSecondary,
    outline = Color(0xFFD0D0D0),
    outlineVariant = DividerLight,
    scrim = Scrim,
    error = NotelyAccentError,
    onError = Color.White,
    errorContainer = Color(0xFFFFE0E0),
    onErrorContainer = NotelyAccentError
)

@Composable
fun SabihonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Force dark theme (Notely is dark-first)
    val colorScheme = NotelyDarkColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SabihonTypography,
        shapes = SabihonShapes,
        content = content
    )
}
