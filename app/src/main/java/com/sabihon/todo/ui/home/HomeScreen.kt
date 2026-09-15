package com.sabihon.todo.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sabihon.todo.core.ui.components.EmptyState
import com.sabihon.todo.core.ui.components.LoadingShimmer
import com.sabihon.todo.core.ui.components.SectionHeader
import com.sabihon.todo.core.ui.components.TaskRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAllTasks: () -> Unit = {},
    onNavigateToCategories: () -> Unit = {},
    onNavigateToTaskDetail: (String) -> Unit = {},
    onNavigateToAddTask: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCategory: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello, ${uiState.greetingName},",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "You have work today",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Filter icon removed as requested – filtering via chips below
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                // 5 Chips: Today, Pending, Completed, All, Overdue
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val chips = listOf(
                            HomeFilter.TODAY to "Today",
                            HomeFilter.PENDING to "Pending",
                            HomeFilter.COMPLETED to "Completed",
                            HomeFilter.ALL to "All",
                            HomeFilter.OVERDUE to "Overdue"
                        )
                        items(chips) { (filter, label) ->
                            FilterChip(
                                selected = uiState.selectedFilter == filter,
                                onClick = { viewModel.onAction(HomeAction.SetFilter(filter)) },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    SectionHeader(
                        title = when (uiState.selectedFilter) {
                            HomeFilter.TODAY -> "Today's Task"
                            HomeFilter.PENDING -> "Pending Tasks"
                            HomeFilter.COMPLETED -> "Completed Tasks"
                            HomeFilter.ALL -> "All Tasks"
                            HomeFilter.OVERDUE -> "Overdue Tasks"
                        },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (uiState.filteredTasks.isEmpty()) {
                    item {
                        EmptyState(
                            title = "No tasks yet",
                            description = when (uiState.selectedFilter) {
                                HomeFilter.TODAY -> "No tasks for today. Tap + to create!"
                                HomeFilter.COMPLETED -> "No completed tasks yet"
                                HomeFilter.PENDING -> "No pending tasks – you're all caught up!"
                                HomeFilter.ALL -> "Tap + to create your first task and stay organized!"
                                HomeFilter.OVERDUE -> "No overdue tasks – great job!"
                            }
                        )
                    }
                } else {
                    items(uiState.filteredTasks, key = { it.id }) { task ->
                        var showMenu by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.animateContentSize()) {
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
                                DropdownMenuItem(
                                    text = { Text(if (task.isCompleted) "Mark Pending" else "Mark Done") },
                                    onClick = {
                                        showMenu = false
                                        viewModel.onAction(HomeAction.ToggleComplete(task.id, !task.isCompleted))
                                    }
                                )
                                DropdownMenuItem(text = { Text("Delete") }, onClick = {
                                    showMenu = false
                                    viewModel.onAction(HomeAction.DeleteTask(task))
                                })
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(100.dp)) }
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
    val cal = java.util.Calendar.getInstance().apply {
        this.timeInMillis = timeMillis
        set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

private fun getEndOfDay(timeMillis: Long): Long {
    val cal = java.util.Calendar.getInstance().apply {
        this.timeInMillis = timeMillis
        set(java.util.Calendar.HOUR_OF_DAY, 23); set(java.util.Calendar.MINUTE, 59)
        set(java.util.Calendar.SECOND, 59); set(java.util.Calendar.MILLISECOND, 999)
    }
    return cal.timeInMillis
}
