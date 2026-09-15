package com.sabihon.todo.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sabihon.todo.core.ui.theme.SoftBlue
import com.sabihon.todo.core.ui.theme.SoftMint
import com.sabihon.todo.core.ui.theme.SoftPink
import com.sabihon.todo.core.ui.theme.SoftYellow

/**
 * Stat tile – 2×2 grid item, rounded 24.dp, pastel bg, small circular icon badge
 * top-left, label bottom-left, count bottom-right in bold.
 */
@Composable
fun PastelStatTile(
    label: String,
    count: Int,
    backgroundColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp)
            .height(110.dp)
    ) {
        // Top-left icon badge
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.7f))
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }

        // Bottom row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }
    }
}

enum class StatType { TODAY, SCHEDULED, ALL, OVERDUE }

@Composable
fun StatTileByType(
    type: StatType,
    count: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (bg, icon, label) = when (type) {
        StatType.TODAY -> Triple(SoftBlue, Icons.Default.AccessTime, "Today")
        StatType.SCHEDULED -> Triple(SoftYellow, Icons.Default.CalendarMonth, "Scheduled")
        StatType.ALL -> Triple(SoftMint, Icons.Default.Layers, "All")
        StatType.OVERDUE -> Triple(SoftPink, Icons.Default.Warning, "Overdue")
    }
    PastelStatTile(
        label = label,
        count = count,
        backgroundColor = bg,
        icon = icon,
        modifier = modifier,
        onClick = onClick
    )
}
