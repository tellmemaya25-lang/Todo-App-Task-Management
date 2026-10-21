package com.sabihon.todo.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.sabihon.todo.core.ui.theme.AccentBlue

/**
 * Fixed circular checkbox – 48dp touch target, 28dp visual, spring animation,
 * accessible role, consistent with pastel design.
 */
@Composable
fun CircleCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) AccentBlue else Color.Transparent,
        label = "checkboxBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) AccentBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        label = "checkboxBorder"
    )
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkboxScale"
    )

    // 48dp touch target for accessibility, 28dp visual circle
    Box(
        modifier = modifier
            .size(48.dp)
            .clickable(
                enabled = enabled,
                role = Role.Checkbox,
                onClick = { onCheckedChange(!checked) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = if (checked) "Completed" else "Mark complete",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
