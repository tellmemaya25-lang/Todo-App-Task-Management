package com.sabihon.todo.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sabihon.todo.core.ui.theme.PriorityHigh
import com.sabihon.todo.core.ui.theme.PriorityHighBg
import com.sabihon.todo.core.ui.theme.PriorityLow
import com.sabihon.todo.core.ui.theme.PriorityLowBg
import com.sabihon.todo.core.ui.theme.PriorityMedium
import com.sabihon.todo.core.ui.theme.PriorityMediumBg
import com.sabihon.todo.core.ui.theme.PriorityUrgent
import com.sabihon.todo.core.ui.theme.PriorityUrgentBg
import com.sabihon.todo.domain.model.Priority

/**
 * Color-coded priority chip.
 */
@Composable
fun PriorityChip(
    priority: Priority,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val (bg, fg) = when (priority) {
        Priority.LOW -> PriorityLowBg to PriorityLow
        Priority.MEDIUM -> PriorityMediumBg to PriorityMedium
        Priority.HIGH -> PriorityHighBg to PriorityHigh
        Priority.URGENT -> PriorityUrgentBg to PriorityUrgent
    }

    val label = when (priority) {
        Priority.LOW -> "Low"
        Priority.MEDIUM -> "Medium"
        Priority.HIGH -> "High"
        Priority.URGENT -> "Urgent"
    }

    val shape = RoundedCornerShape(100.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) fg else bg)
            .then(
                if (selected) Modifier.border(0.dp, Color.Transparent, shape)
                else Modifier.border(1.dp, fg.copy(alpha = 0.3f), shape)
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Color.White else fg
        )
    }
}
