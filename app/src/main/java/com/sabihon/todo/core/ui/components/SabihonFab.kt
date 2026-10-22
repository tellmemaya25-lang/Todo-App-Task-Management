package com.sabihon.todo.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.CubicOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.sabihon.todo.core.ui.theme.AccentBlue

/**
 * Circular FAB – accentBlue with white plus, 6.dp elevation.
 * Scales to 0.95 on press with 160ms ease-out animation for tactile feedback.
 * Scales in from 0.5 on first appearance for delight (respects accessibility).
 */
@Composable
fun SabihonFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Add task"
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }
    var isMounted by remember { mutableStateOf(false) }
    val isReducedMotion = LocalConfiguration.current.animationDurationScale == 0f

    // Animate on press
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release -> isPressed = false
                is PressInteraction.Cancel -> isPressed = false
            }
        }
    }

    // Animate on mount
    LaunchedEffect(Unit) {
        isMounted = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = tween(160, easing = CubicOut),
        label = "fabScale"
    )

    val enterScale by animateFloatAsState(
        targetValue = if (isMounted) 1.0f else 0.5f,
        animationSpec = if (isReducedMotion) tween(0) else tween(200, easing = CubicOut),
        label = "fabEnter"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .graphicsLayer { scaleX = enterScale * scale; scaleY = enterScale * scale },
        shape = CircleShape,
        containerColor = AccentBlue,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = contentDescription
        )
    }
}
