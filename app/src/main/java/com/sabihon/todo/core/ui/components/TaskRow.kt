package com.sabihon.todo.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sabihon.todo.core.ui.theme.AccentBlue
import com.sabihon.todo.core.ui.theme.SoftBlue
import com.sabihon.todo.domain.model.SubTask

/**
 * TaskRow – strong visual hierarchy, only title + short description + percentage + status
 * - Title: bold large, primary
 * - Short description: small gray, 1 line
 * - Percentage: big on right, as big as title+description, 50% + 2/4
 * - Status: chip / color indicator
 * - Gradient + animation when done
 * - Each task own card 24.dp
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskRow(
    title: String,
    isCompleted: Boolean,
    timeLabel: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    subTasks: List<SubTask> = emptyList(),
    categoryLabel: String? = null,
    onCheckedChange: (Boolean) -> Unit = {},
    onAddSubTask: (() -> Unit)? = null,
    onSubTaskChecked: ((String, Boolean) -> Unit)? = null,
    onMoreClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val totalSubs = subTasks.size
    val doneSubs = subTasks.count { it.isDone }
    val progress = if (totalSubs > 0) doneSubs.toFloat() / totalSubs else if (isCompleted) 1f else 0f
    val hasSubTasks = totalSubs > 0

    // Status logic
    val statusText = when {
        isCompleted -> "Completed"
        hasSubTasks && progress > 0f && progress < 1f -> "In Progress"
        hasSubTasks && progress == 0f -> "Pending"
        else -> "Pending"
    }
    val statusColor = when {
        isCompleted -> Color(0xFF4CAF50)
        hasSubTasks && progress > 0f -> AccentBlue
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val containerColor by animateColorAsState(
        targetValue = if (isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        animationSpec = tween(400),
        label = "containerColor"
    )

    val checkScale by animateFloatAsState(
        targetValue = if (isCompleted) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkScale"
    )

    val gradientBrush = if (isCompleted) {
        Brush.linearGradient(
            colors = listOf(
                AccentBlue.copy(alpha = 0.15f),
                SoftBlue.copy(alpha = 0.3f),
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            )
        )
    } else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (gradientBrush != null) Modifier.background(gradientBrush) else Modifier)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { onClick?.invoke() },
                        onLongClick = { onLongClick?.invoke() ?: onMoreClick?.invoke() }
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: title + short description + status – strong hierarchy
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Title – bold large, strong hierarchy
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                        ),
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Short description – 1 line only, like screenshot "Cybersecurity" or "from: ..."
                    val shortDesc = when {
                        !categoryLabel.isNullOrBlank() -> categoryLabel
                        !description.isNullOrBlank() -> description.take(40)
                        timeLabel.isNotBlank() && timeLabel != "Today" -> timeLabel
                        else -> null
                    }

                    if (shortDesc != null) {
                        Text(
                            text = shortDesc,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Status row – small, subtle but clear hierarchy
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        // Status dot
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = statusColor
                        )
                        if (hasSubTasks) {
                            Text(
                                text = "• $doneSubs/$totalSubs",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                // Right: percentage – big as title+description, strong visual hierarchy like screenshot 50% 2/4
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .scale(checkScale),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasSubTasks) {
                        // Big ring – 56.dp as big as title+desc
                        Canvas(modifier = Modifier.size(56.dp)) {
                            val strokeWidth = 4.dp.toPx()
                            // Background track
                            drawCircle(
                                color = Color.Gray.copy(alpha = 0.12f),
                                style = Stroke(width = strokeWidth)
                            )
                            // Progress
                            if (progress > 0f) {
                                drawArc(
                                    color = AccentBlue,
                                    startAngle = -90f,
                                    sweepAngle = 360f * progress,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                        }
                        // Center: 50% + 2/4 like screenshot top right
                        if (isCompleted || progress >= 1f) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(AccentBlue, AccentBlue.copy(alpha = 0.85f))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$doneSubs/$totalSubs",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        // No sub-tasks – show status circle big
                        if (isCompleted) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(AccentBlue, Color(0xFF5A8CFF))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            // Pending circle – empty, subtle
                            Canvas(modifier = Modifier.size(48.dp)) {
                                drawCircle(
                                    color = Color.Gray.copy(alpha = 0.25f),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
