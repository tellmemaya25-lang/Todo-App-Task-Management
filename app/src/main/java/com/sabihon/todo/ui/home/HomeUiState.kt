package com.sabihon.todo.ui.home

import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.usecase.DashboardCounts

enum class HomeFilter {
    TODAY, COMPLETED, PENDING, ALL
}

/**
 * Immutable UiState for Home Dashboard.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val greetingName: String = "User",
    val counts: DashboardCounts = DashboardCounts(),
    val allTasks: List<Task> = emptyList(),
    val todayTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val selectedFilter: HomeFilter = HomeFilter.TODAY,
    val errorMessage: String? = null,
    val showUndo: Boolean = false,
    val lastDeletedTask: Task? = null
)

sealed interface HomeAction {
    data class ToggleComplete(val taskId: String, val isCompleted: Boolean) : HomeAction
    data class DeleteTask(val task: Task) : HomeAction
    data object UndoDelete : HomeAction
    data class AddSubTask(val taskId: String, val subTaskTitle: String) : HomeAction
    data class ToggleSubTask(val taskId: String, val subTaskId: String, val isDone: Boolean) : HomeAction
    data class SetFilter(val filter: HomeFilter) : HomeAction
}
