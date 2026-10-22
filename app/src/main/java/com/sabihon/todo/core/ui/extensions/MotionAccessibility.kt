package com.sabihon.todo.core.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Detects if the user has enabled "Reduce motion" in device accessibility settings.
 * Returns true when animations should be reduced or skipped (duration set to 0).
 *
 * Respects WCAG 2.1 guideline 2.3.3 (Animation from Interactions).
 */
@Composable
fun useReducedMotion(): Boolean {
    val animationDuration = LocalConfiguration.current.animationDurationScale
    return animationDuration == 0f
}
