package com.sabihon.todo.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Sabihon theme with Material 3 – dynamic color opt-out, custom pastel palette.
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
    background = BgLight,
    onBackground = TextPrimaryLight,
    surface = BgLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    error = PriorityUrgent,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    primaryContainer = SoftBlueDark,
    onPrimaryContainer = TextPrimaryDark,
    secondary = SoftMint,
    onSecondary = TextPrimaryDark,
    secondaryContainer = DeepGreenDark,
    onSecondaryContainer = Color.White,
    tertiary = SoftLilacDark,
    onTertiary = TextPrimaryDark,
    background = BgDark,
    onBackground = TextPrimaryDark,
    surface = BgDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    error = PriorityUrgent,
    onError = Color.White
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
