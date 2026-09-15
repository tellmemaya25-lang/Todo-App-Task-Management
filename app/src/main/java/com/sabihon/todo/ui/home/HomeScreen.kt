package com.sabihon.todo.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sabihon.todo.core.ui.components.EmptyState
import com.sabihon.todo.core.ui.components.LoadingShimmer
import com.sabihon.todo.core.ui.components.SabihonFab
import com.sabihon.todo.core.ui.components.SectionHeader
import com.sabihon.todo.core.ui.components.StatTileByType
import com.sabihon.todo.core.ui.components.StatType
import com.sabihon.todo.core.ui.components.TaskRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Home / Dashboard – Screen 1 from spec.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToAllTasks: () -> Unit = {},
    onNavigateToCategories: () -> Unit = {},
    onNavigateToTaskDetail: (String) -> Unit = {},
    onNavigateToAddTask: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCategory: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(uiState.showUndo) {
        if (uiState.showUndo) {
            val result = snackbarHostState.showSnackbar(
                message = "Task deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.onAction(HomeAction.UndoDelete)
            } else {
                viewModel.dismissUndo()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            SabihonFab(onClick = onNavigateToAddTask)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingShimmer(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Greeting header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hello, ${uiState.greetingName},",
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "You have work today",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row {
                            IconButton(onClick = onNavigateToHistory) {
                                Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(onClick = onNavigateToProfile) {
                                    Icon(Icons.Filled.Person, contentDescription = "Avatar")
                                }
                            }
                        }
                    }
                }

                // 2x2 stat tiles
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatTileByType(
                                type = StatType.TODAY,
                                count = uiState.counts.today,
                                modifier = Modifier.weight(1f),
                                onClick = { /* filter today */ }
                            )
                            StatTileByType(
                                type = StatType.SCHEDULED,
                                count = uiState.counts.scheduled,
                                modifier = Modifier.weight(1f),
                                onClick = { /* filter scheduled */ }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatTileByType(
                                type = StatType.ALL,
                                count = uiState.counts.all,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToAllTasks
                            )
                            StatTileByType(
                                type = StatType.OVERDUE,
                                count = uiState.counts.overdue,
                                modifier = Modifier.weight(1f),
                                onClick = { /* filter overdue */ }
                            )
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(24.dp))
                    SectionHeader(
                        title = "Today's Task",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (uiState.todayTasks.isEmpty()) {
                    item {
                        EmptyState(
                            title = "No tasks yet",
                            description = "Tap + to create your first task and stay organized!"
                        )
                    }
                } else {
                    items(uiState.todayTasks, key = { it.id }) { task ->
                        var showMenu by remember { mutableStateOf(false) }

                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                when (value) {
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        // Swipe to complete
                                        viewModel.onAction(HomeAction.ToggleComplete(task.id, !task.isCompleted))
                                        false // Don't dismiss, just toggle
                                    }
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        viewModel.onAction(HomeAction.DeleteTask(task))
                                        true
                                    }
                                    else -> false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFF44336).copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                }
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color)
                                )
                            },
                            modifier = Modifier.animateContentSize()
                        ) {
                            Box {
                                TaskRow(
                                    title = task.title,
                                    isCompleted = task.isCompleted,
                                    timeLabel = formatTimeLabel(task.dueAt),
                                    description = task.description.takeIf { it.isNotBlank() },
                                    subTasks = task.subTasks,
                                    onCheckedChange = { checked ->
                                        viewModel.onAction(HomeAction.ToggleComplete(task.id, checked))
                                    },
                                    onAddSubTask = {
                                        // For Loop 4, simple inline add with placeholder
                                        viewModel.onAction(HomeAction.AddSubTask(task.id, "New Sub-Task"))
                                    },
                                    onSubTaskChecked = { subId, done ->
                                        viewModel.onAction(HomeAction.ToggleSubTask(task.id, subId, done))
                                    },
                                    onMoreClick = { showMenu = true },
                                    onClick = { onNavigateToTaskDetail(task.id) }
                                )
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(text = { Text("Edit") }, onClick = {
                                        showMenu = false
                                        onNavigateToTaskDetail(task.id)
                                    })
                                    DropdownMenuItem(text = { Text("Duplicate") }, onClick = {
                                        showMenu = false
                                        // Duplicate logic could be in ViewModel
                                    })
                                    DropdownMenuItem(text = { Text("Delete") }, onClick = {
                                        showMenu = false
                                        viewModel.onAction(HomeAction.DeleteTask(task))
                                    })
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}

private fun formatTimeLabel(dueAt: Long?): String {
    if (dueAt == null) return "Today"
    val now = System.currentTimeMillis()
    val startOfToday = getStartOfDay(now)
    val startOfYesterday = startOfToday - 24 * 60 * 60 * 1000L
    val endOfToday = getEndOfDay(now)

    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val timeStr = timeFormat.format(Date(dueAt))

    return when {
        dueAt in startOfToday..endOfToday -> "Today • $timeStr"
        dueAt in startOfYesterday until startOfToday -> "Yesterday • $timeStr"
        else -> {
            val dateFormat = SimpleDateFormat("MMM d • h:mm a", Locale.getDefault())
            dateFormat.format(Date(dueAt))
        }
    }
}

private fun getStartOfDay(timeMillis: Long): Long {
    val cal = java.util.Calendar.getInstance()
    cal.timeInMillis = timeMillis
    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
    cal.set(java.util.Calendar.MINUTE, 0)
    cal.set(java.util.Calendar.SECOND, 0)
    cal.set(java.util.Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun getEndOfDay(timeMillis: Long): Long {
    val cal = java.util.Calendar.getInstance()
    cal.timeInMillis = timeMillis
    cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
    cal.set(java.util.Calendar.MINUTE, 59)
    cal.set(java.util.Calendar.SECOND, 59)
    cal.set(java.util.Calendar.MILLISECOND, 999)
    return cal.timeInMillis
}
