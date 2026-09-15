package com.sabihon.todo.core.ui.components

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sabihon.todo.core.ui.theme.AccentBlue
import com.sabihon.todo.domain.model.SubTask

/**
 * TaskRow – each task as its own card, inspired by screenshot
 * - 24.dp radius, elevation 0, surfaceVariant 0.6
 * - Left: interactive circular percentage + checkbox (blue check / empty)
 * - Center: title + subtitle from:
 * - Sub-tasks: design from screenshot – blue check circle / empty circle, title + from:, divider, Add task
 * - Long press to edit/delete/mark done (no 3 dots)
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { onClick?.invoke() },
                    onLongClick = { onLongClick?.invoke() ?: onMoreClick?.invoke() }
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: percentage ring + checkbox – screenshot style
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasSubTasks) {
                        Canvas(modifier = Modifier.size(36.dp)) {
                            val strokeWidth = 3.dp.toPx()
                            drawCircle(
                                color = Color.Gray.copy(alpha = 0.2f),
                                style = Stroke(width = strokeWidth)
                            )
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
                        if (isCompleted || progress >= 1f) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(AccentBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        CircleCheckbox(
                            checked = isCompleted,
                            onCheckedChange = onCheckedChange
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                        ),
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    val subtitle = when {
                        !categoryLabel.isNullOrBlank() -> "from: $categoryLabel"
                        !description.isNullOrBlank() -> description
                        timeLabel.isNotBlank() -> timeLabel
                        else -> null
                    }

                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Sub-tasks – exact design from screenshot
            if (hasSubTasks) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                ) {
                    subTasks.forEachIndexed { index, sub ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { onSubTaskChecked?.invoke(sub.id, !sub.isDone) }
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            // Blue check / empty circle like screenshot
                            Box(
                                modifier = Modifier.size(22.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (sub.isDone) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(AccentBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                } else {
                                    Canvas(modifier = Modifier.size(20.dp)) {
                                        drawCircle(
                                            color = Color.Gray.copy(alpha = 0.4f),
                                            style = Stroke(width = 1.5.dp.toPx())
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = sub.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        textDecoration = if (sub.isDone) TextDecoration.LineThrough else null
                                    ),
                                    color = if (sub.isDone) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                // from: subtitle – use categoryLabel if provided, else show generic like screenshot
                                Text(
                                    text = if (!categoryLabel.isNullOrBlank()) "from: $categoryLabel" else "from: ${title.take(20)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (index < subTasks.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                    }

                    // Add task row – empty circle + Add task gray like screenshot
                    if (onAddSubTask != null) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(onClick = { onAddSubTask.invoke() })
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Canvas(modifier = Modifier.size(20.dp)) {
                                drawCircle(
                                    color = Color.Gray.copy(alpha = 0.3f),
                                    style = Stroke(width = 1.2.dp.toPx())
                                )
                            }
                            Text(
                                text = "Add task",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                if (onAddSubTask != null) {
                    TextButton(
                        onClick = onAddSubTask,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "+ Add task",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
