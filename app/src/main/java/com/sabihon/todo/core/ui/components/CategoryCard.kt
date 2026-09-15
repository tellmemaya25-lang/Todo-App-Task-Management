package com.sabihon.todo.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sabihon.todo.core.ui.theme.parseColorHex

/**
 * Category card – full-width ~110.dp tall, rounded 24.dp, pastel or deepGreen bg
 * with white text, "{n} Completed" pill top-right, "{n} Notes" bottom-left,
 * ↗ button bottom-right.
 */
@Composable
fun CategoryCard(
    name: String,
    colorHex: String,
    taskCount: Int,
    completedCount: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val bgColor = parseColorHex(colorHex)
    val isDarkBg = colorHex.equals("#2F6B4F", ignoreCase = true) || bgColor == Color(0xFF2F6B4F)
    val contentColor = if (isDarkBg) Color.White else Color.Black
    val secondaryColor = if (isDarkBg) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.6f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp)
    ) {
        // Top row: name + completed pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(
                        if (isDarkBg) Color.White.copy(alpha = 0.2f)
                        else Color.Black.copy(alpha = 0.05f)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$completedCount Completed",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor
                )
            }
        }

        // Bottom row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$taskCount Notes",
                style = MaterialTheme.typography.bodyMedium,
                color = secondaryColor,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onClick?.invoke() },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkBg) Color.White.copy(alpha = 0.15f)
                        else Color.Black.copy(alpha = 0.05f)
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowOutward,
                    contentDescription = "Open category",
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
