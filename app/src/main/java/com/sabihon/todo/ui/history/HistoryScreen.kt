package com.sabihon.todo.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sabihon.todo.core.ui.components.TaskRow
import com.sabihon.todo.core.ui.theme.AccentBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit = {},
    onTaskClick: (String) -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Statistics", style = MaterialTheme.typography.titleLarge)
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            StatItem(label = "This Week", value = "${uiState.stats.completedThisWeek}")
                            StatItem(label = "Completion", value = "${(uiState.stats.completionRate * 100).toInt()}%")
                            StatItem(label = "Streak", value = "${uiState.stats.currentStreak} days")
                        }
                        // Simple 7-day bar chart drawn with Canvas
                        Text("Last 7 days", style = MaterialTheme.typography.labelMedium)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val max = (uiState.stats.dailyCounts.maxOrNull() ?: 1).coerceAtLeast(1)
                                val barWidth = size.width / 7 * 0.6f
                                val spacing = size.width / 7
                                uiState.stats.dailyCounts.forEachIndexed { index, count ->
                                    val barHeight = (count.toFloat() / max) * size.height * 0.8f
                                    drawRect(
                                        color = AccentBlue,
                                        topLeft = Offset(
                                            x = index * spacing + (spacing - barWidth) / 2,
                                            y = size.height - barHeight
                                        ),
                                        size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Grouped tasks
            uiState.grouped.forEach { (groupName, tasks) ->
                if (tasks.isNotEmpty()) {
                    item {
                        Text(groupName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                    }
                    items(tasks, key = { it.id }) { task ->
                        Column {
                            TaskRow(
                                title = task.title,
                                isCompleted = task.isCompleted,
                                timeLabel = task.completedAt?.let { java.text.SimpleDateFormat("h:mm a").format(java.util.Date(it)) } ?: "Completed",
                                onClick = { onTaskClick(task.id) }
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(start = 60.dp)) {
                                TextButton(onClick = { viewModel.restoreTask(task.id) }) {
                                    Text("Restore")
                                }
                                TextButton(onClick = { viewModel.permanentlyDelete(task.id) }) {
                                    Text("Delete", color = Color.Red)
                                }
                            }
                        }
                    }
                }
            }

            // Activity log
            if (uiState.activityLogs.isNotEmpty()) {
                item {
                    Text("Activity Log", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                }
                items(uiState.activityLogs) { log ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text("${log.action.name} – ${log.taskTitle}", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            java.text.SimpleDateFormat("MMM d, h:mm a").format(java.util.Date(log.timestamp)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
