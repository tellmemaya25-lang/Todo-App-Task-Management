package com.sabihon.todo.core.ui.extensions

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.CubicOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Scale-on-press modifier – animates scale from 1.0 to [scale] on press.
 * Reusable for all clickable elements (tasks, cards, chips, tiles).
 *
 * @param scale Target scale on press (default 0.97 for cards, 0.95 for primary actions)
 * @param interactionSource Interaction source from .clickable() to detect press events
 */
fun Modifier.scaleOnPress(
    scale: Float = 0.97f,
    interactionSource: InteractionSource
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val isReducedMotion = run {
        // Detect reduced motion inside composed scope
        androidx.compose.ui.platform.LocalConfiguration.current.animationDurationScale == 0f
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release -> isPressed = false
                is PressInteraction.Cancel -> isPressed = false
            }
        }
    }

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) scale else 1.0f,
        animationSpec = tween(if (isReducedMotion) 0 else 160, easing = CubicOut),
        label = "pressScale"
    )

    graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
}
