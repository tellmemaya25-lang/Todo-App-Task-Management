package com.sabihon.todo.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Sabihon theme with Material 3 – full light & dark mode UI support
 * Light: white background, pastel containers
 * Dark: #121214 background, desaturated pastel containers, high contrast text
 */

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    primaryContainer = SoftBlue,
    onPrimaryContainer = TextPrimaryLight,
    secondary = DeepGreen,
    onSecondary = Color.White,
    secondaryContainer = SoftMint,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = SoftLilac,
    onTertiary = TextPrimaryLight,
    tertiaryContainer = SoftPink,
    onTertiaryContainer = TextPrimaryLight,
    background = BgLight,
    onBackground = TextPrimaryLight,
    surface = BgLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    surfaceContainer = Color(0xFFF0F1F5),
    surfaceContainerHigh = Color(0xFFE8E9ED),
    outline = Color(0xFFD1D5DB),
    outlineVariant = DividerLight,
    scrim = Scrim,
    error = PriorityUrgent,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    primaryContainer = SoftBlueDark,
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = SoftMint,
    onSecondary = Color(0xFF0A1F16),
    secondaryContainer = DeepGreenDark,
    onSecondaryContainer = Color(0xFFCFF5E7),
    tertiary = SoftLilac,
    onTertiary = Color(0xFF2D1B4E),
    tertiaryContainer = SoftLilacDark,
    onTertiaryContainer = Color(0xFFE5DBFF),
    background = BgDark,
    onBackground = TextPrimaryDark,
    surface = BgDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    surfaceContainer = Color(0xFF1E1F25),
    surfaceContainerHigh = Color(0xFF2A2B33),
    surfaceContainerHighest = Color(0xFF35363F),
    outline = Color(0xFF3A3B44),
    outlineVariant = DividerDark,
    scrim = Scrim,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun SabihonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SabihonTypography,
        shapes = SabihonShapes,
        content = content
    )
}
