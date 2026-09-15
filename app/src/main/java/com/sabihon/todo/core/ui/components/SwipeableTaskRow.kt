package com.sabihon.todo.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sabihon.todo.core.ui.theme.AccentBlue
import com.sabihon.todo.domain.model.SubTask
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Swipeable task row – stable implementation using draggable + Animatable
 * - Swipe right (positive offset) -> Mark as Done (blue)
 * - Swipe left (negative offset) -> Edit (blue) + Delete (red)
 * - Works with LazyColumn vertical scroll
 */
@Composable
fun SwipeableTaskRow(
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
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onToggleComplete: ((Boolean) -> Unit)? = null
) {
    val density = LocalDensity.current
    val startThreshold = with(density) { 100.dp.toPx() } // swipe right threshold
    val endThreshold = with(density) { -160.dp.toPx() } // swipe left threshold
    val maxStart = with(density) { 100.dp.toPx() }
    val maxEnd = with(density) { -160.dp.toPx() }

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isDragging by remember { androidx.compose.runtime.mutableStateOf(false) }

    // Auto snap back after action if needed
    LaunchedEffect(isCompleted) {
        // When task toggled, snap back to center
        if (offsetX.value != 0f) {
            scope.launch {
                offsetX.animateTo(0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
    ) {
        // Background actions – no overlap with percentage (percentage now left side)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFE8EEFF))
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left – Mark Done (visible when swiped right) – percentage is left, so keep small gap
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AccentBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Mark Done",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Right – Edit + Delete (visible when swiped left) – percentage now left, so no overlap
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        }
                        onEdit?.invoke()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AccentBlue.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        }
                        onDelete?.invoke()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE57373))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Foreground draggable card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    state = rememberDraggableState { delta ->
                        // Only allow horizontal drag, clamp
                        val newValue = (offsetX.value + delta).coerceIn(maxEnd, maxStart)
                        scope.launch {
                            offsetX.snapTo(newValue)
                        }
                    },
                    orientation = Orientation.Horizontal,
                    onDragStarted = { isDragging = true },
                    onDragStopped = { velocity ->
                        isDragging = false
                        scope.launch {
                            when {
                                offsetX.value > startThreshold / 2 -> {
                                    // Swipe right -> mark done
                                    onToggleComplete?.invoke(!isCompleted)
                                    offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                                offsetX.value < endThreshold / 2 -> {
                                    // Swipe left -> reveal edit/delete, stay open
                                    offsetX.animateTo(maxEnd, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                                else -> {
                                    // Snap back to center
                                    offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                            }
                        }
                    }
                )
        ) {
            TaskRow(
                title = title,
                isCompleted = isCompleted,
                timeLabel = timeLabel,
                description = description,
                subTasks = subTasks,
                categoryLabel = categoryLabel,
                onCheckedChange = onCheckedChange,
                onAddSubTask = onAddSubTask,
                onSubTaskChecked = onSubTaskChecked,
                onClick = {
                    // If swiped open, close first, else click
                    if (offsetX.value != 0f) {
                        scope.launch {
                            offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        }
                    } else {
                        onClick?.invoke()
                    }
                },
                onLongClick = {
                    if (offsetX.value == 0f) {
                        onLongClick?.invoke()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
