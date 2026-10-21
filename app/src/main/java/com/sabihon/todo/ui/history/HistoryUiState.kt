package com.sabihon.todo.ui.history

import com.sabihon.todo.domain.model.ActivityLog
import com.sabihon.todo.domain.model.Task

data class HistoryUiState(
    val isLoading: Boolean = true,
    val completedTasks: List<Task> = emptyList(),
    val grouped: Map<String, List<Task>> = emptyMap(), // Today, Yesterday, Earlier
    val activityLogs: List<ActivityLog> = emptyList(),
    val stats: HistoryStats = HistoryStats()
)

data class HistoryStats(
    val completedThisWeek: Int = 0,
    val completionRate: Float = 0f,
    val currentStreak: Int = 0,
    val dailyCounts: List<Int> = List(7) { 0 } // 7-day bar chart
)
