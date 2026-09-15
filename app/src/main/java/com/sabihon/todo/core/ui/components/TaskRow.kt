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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.sabihon.todo.domain.model.SubTask

/**
 * TaskRow – strong hierarchy, only title + short desc + percentage + status
 * - Background #c7dcff per request
 * - Percentage moved to left, big as title+description (56.dp), with animation
 * - Gradient + animation when done
 * - No overlap with swipe actions (percentage left, swipe actions right)
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
    hasOuterPadding: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {},
    onAddSubTask: (() -> Unit)? = null,
    onSubTaskChecked: ((String, Boolean) -> Unit)? = null,
    onMoreClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val totalSubs = subTasks.size
    val doneSubs = subTasks.count { it.isDone }
    val rawProgress = if (totalSubs > 0) doneSubs.toFloat() / totalSubs else if (isCompleted) 1f else 0f
    val hasSubTasks = totalSubs > 0

    // Animated percentage – for animation when progress changes
    var targetProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(rawProgress) {
        targetProgress = rawProgress
    }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 800),
        label = "progressAnim"
    )

    val statusText = when {
        isCompleted -> "Completed"
        hasSubTasks && animatedProgress > 0f && animatedProgress < 1f -> "In Progress"
        hasSubTasks && animatedProgress == 0f -> "Pending"
        else -> "Pending"
    }
    val statusColor = when {
        isCompleted -> Color(0xFF4CAF50)
        hasSubTasks && animatedProgress > 0f -> AccentBlue
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    // Background #e3f5ff per request, with gradient when done
    val baseBackground = Color(0xFFE3F5FF)
    val completedBackground = Color(0xFFC7DCFF)

    val containerColor by animateColorAsState(
        targetValue = if (isCompleted) completedBackground else baseBackground,
        animationSpec = tween(400),
        label = "containerColor"
    )

    val checkScale by animateFloatAsState(
        targetValue = if (isCompleted) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkScale"
    )

    val gradientBrush = if (isCompleted) {
        Brush.linearGradient(
            colors = listOf(
                AccentBlue.copy(alpha = 0.2f),
                Color(0xFFE3F5FF),
                Color(0xFFC7DCFF)
            )
        )
    } else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (hasOuterPadding) Modifier.padding(horizontal = 16.dp, vertical = 6.dp) else Modifier),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (gradientBrush != null) Modifier.background(gradientBrush) else Modifier)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                    // Percentage in front of title – big as title+description, with animation
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .scale(checkScale),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasSubTasks) {
                            Canvas(modifier = Modifier.size(56.dp)) {
                                val strokeWidth = 4.dp.toPx()
                                drawCircle(
                                    color = Color.Gray.copy(alpha = 0.15f),
                                    style = Stroke(width = strokeWidth)
                                )
                                if (animatedProgress > 0f) {
                                    drawArc(
                                        color = AccentBlue,
                                        startAngle = -90f,
                                        sweepAngle = 360f * animatedProgress,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                            }
                            if (isCompleted || animatedProgress >= 1f) {
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
                                        text = "${(animatedProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        color = Color(0xFF101114)
                                    )
                                    Text(
                                        text = "$doneSubs/$totalSubs",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }
                        } else {
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
                                Canvas(modifier = Modifier.size(48.dp)) {
                                    drawCircle(
                                        color = Color.Gray.copy(alpha = 0.35f),
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    // Title + short desc + status – strong hierarchy
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                lineHeight = 20.sp,
                                textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                            ),
                            color = if (isCompleted) Color(0xFF6B7280).copy(alpha = 0.6f)
                            else Color(0xFF101114),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        val shortDesc = when {
                            !categoryLabel.isNullOrBlank() -> categoryLabel
                            !description.isNullOrBlank() -> description
                            timeLabel.isNotBlank() && timeLabel != "Today" -> timeLabel
                            else -> null
                        }

                        if (shortDesc != null) {
                            Text(
                                text = shortDesc,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 16.sp
                                ),
                                color = Color(0xFF6B7280).copy(alpha = 0.9f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Text(
                                text = "$statusText • $doneSubs/$totalSubs",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = statusColor
                            )
                        }
                    }
                }

                // Horizontal divider to task – per request add horizontal dividers to task
                androidx.compose.material3.HorizontalDivider(
                    thickness = 0.5.dp,
                    color = Color.Gray.copy(alpha = 0.12f)
                )
            }
        }
    }
}
