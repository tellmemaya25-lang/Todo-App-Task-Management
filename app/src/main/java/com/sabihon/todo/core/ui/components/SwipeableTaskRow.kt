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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
 * Swipeable task row – centered swipe right icons + remove swipe left per request
 * - Remove swipe left (mark done) and its icon – only swipe left reveals edit/delete centered
 * - Background edit/delete icons centered in container, fill vertical height, no bg/corner radius
 * - Second screenshot shows light blue container with blue edit + red delete centered
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
    // Only left swipe (negative) – remove right swipe (mark done) per request
    val endThreshold = with(density) { -80.dp.toPx() }
    val maxEnd = with(density) { -140.dp.toPx() }
    val maxStart = 0f

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(isCompleted) {
        if (offsetX.value != 0f) {
            scope.launch {
                offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFE3F5FF))
    ) {
        // Background – centered edit/delete icons per request, remove swipe left icon
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F5FF))
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        }
                        onEdit?.invoke()
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = AccentBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        }
                        onDelete?.invoke()
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    state = rememberDraggableState { delta ->
                        val newValue = (offsetX.value + delta).coerceIn(maxEnd, maxStart)
                        scope.launch { offsetX.snapTo(newValue) }
                    },
                    orientation = Orientation.Horizontal,
                    onDragStarted = { isDragging = true },
                    onDragStopped = {
                        isDragging = false
                        scope.launch {
                            when {
                                offsetX.value < endThreshold -> {
                                    offsetX.animateTo(maxEnd, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                                else -> {
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
                hasOuterPadding = false,
                onCheckedChange = onCheckedChange,
                onAddSubTask = onAddSubTask,
                onSubTaskChecked = onSubTaskChecked,
                onClick = {
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
