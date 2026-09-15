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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.matchParentSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
 * Swipeable task row – centered icons + fill vertical height per request
 * - Outer Box padding 16dp/6dp clip 24dp bg #E3F5FF
 * - Background Row now matchParentSize + fillMaxHeight centered vertically, so icons fill container height
 * - Icons 56dp (was 48dp) centered, with 12dp horizontal padding, vertical fill
 * - Left swipe Edit/Delete blue #3B82F6 + red #E57373 centered and fill height, right swipe Mark Done
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
    val startThreshold = with(density) { 100.dp.toPx() }
    val endThreshold = with(density) { -160.dp.toPx() }
    val maxStart = with(density) { 100.dp.toPx() }
    val maxEnd = with(density) { -160.dp.toPx() }

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isDragging by remember { androidx.compose.runtime.mutableStateOf(false) }

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
        // Background actions – fill vertical height + centered per request
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFFE3F5FF))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left – Mark Done, centered, fills height
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AccentBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Mark Done",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            // Right – Edit + Delete, centered, fills height
            Row(
                modifier = Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AccentBlue)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            }
                            onEdit?.invoke()
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE57373)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            }
                            onDelete?.invoke()
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
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
                                offsetX.value > startThreshold / 2 -> {
                                    // Dont mark as complete when all task are not done
                                    val hasIncomplete = subTasks.isNotEmpty() && subTasks.any { !it.isDone }
                                    if (!isCompleted && !hasIncomplete) {
                                        onToggleComplete?.invoke(true)
                                    }
                                    offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                                offsetX.value < endThreshold / 2 -> {
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
