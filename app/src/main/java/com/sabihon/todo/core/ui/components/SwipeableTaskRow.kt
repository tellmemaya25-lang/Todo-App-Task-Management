package com.sabihon.todo.core.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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

enum class DragAnchors {
    Center,
    Start, // swipe right -> mark done
    End // swipe left -> edit/delete
}

@OptIn(ExperimentalFoundationApi::class)
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
    val actionWidth = 80.dp
    val startActionWidth = 100.dp
    val endActionWidth = 160.dp

    val startPx = with(density) { startActionWidth.toPx() }
    val endPx = with(density) { -endActionWidth.toPx() }

    val state = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Center,
            anchors = DraggableAnchors {
                DragAnchors.Start at startPx
                DragAnchors.Center at 0f
                DragAnchors.End at endPx
            },
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    val scope = rememberCoroutineScope()

    // Auto-trigger actions when swiped beyond threshold and released
    LaunchedEffect(state.currentValue) {
        when (state.currentValue) {
            DragAnchors.Start -> {
                // Swipe right -> mark as done/pending
                onToggleComplete?.invoke(!isCompleted)
                // Snap back
                state.animateTo(DragAnchors.Center)
            }
            DragAnchors.End -> {
                // Keep open to show edit/delete – don't auto trigger, let user tap action
            }
            DragAnchors.Center -> {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        // Background actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left action – Mark as Done (when swiped right)
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
            }

            // Right actions – Edit + Delete (when swiped left)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                IconButton(
                    onClick = {
                        scope.launch { state.animateTo(DragAnchors.Center) }
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
                        scope.launch { state.animateTo(DragAnchors.Center) }
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

        // Foreground task card – draggable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = state.requireOffset().roundToInt(), y = 0) }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal
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
                onClick = onClick,
                onLongClick = onLongClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
