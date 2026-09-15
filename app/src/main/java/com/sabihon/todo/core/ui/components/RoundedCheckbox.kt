package com.sabihon.todo.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sabihon.todo.core.ui.theme.AccentBlue

/**
 * Rounded corner checkbox – 4.dp radius, blue when checked, as per screenshot
 * Used for all checkboxes per request "use rounded corner for all checkboxes"
 */
@Composable
fun RoundedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (checked) AccentBlue else Color.Transparent)
            .border(
                width = 1.5.dp,
                color = if (checked) AccentBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

/**
 * Larger version for sub-tasks list – matches screenshot blue rounded checkbox
 */
@Composable
fun SubTaskRoundedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(if (checked) AccentBlue else Color.Transparent)
            .border(
                width = 1.5.dp,
                color = if (checked) AccentBlue else Color.Gray.copy(alpha = 0.4f),
                shape = RoundedCornerShape(5.dp)
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
