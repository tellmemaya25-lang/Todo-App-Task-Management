package com.sabihon.todo.ui.home

import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.usecase.DashboardCounts

enum class HomeFilter {
    TODAY, COMPLETED, PENDING, ALL, OVERDUE
}

/**
 * Immutable UiState for Home Dashboard.
 * Supports all 7 UI states: ideal, empty, loading, error, offline, success, permission-denied
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val greetingName: String = "User",
    val counts: DashboardCounts = DashboardCounts(),
    val allTasks: List<Task> = emptyList(),
    val todayTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val selectedFilter: HomeFilter = HomeFilter.ALL,
    val errorMessage: String? = null,
    val showUndo: Boolean = false,
    val lastDeletedTask: Task? = null,
    // Tier 2: Additional state properties
    val isOffline: Boolean = false,
    val hasError: Boolean = false,
    val showSuccessMessage: Boolean = false,
    val successMessage: String = ""
)

sealed interface HomeAction {
    data class ToggleComplete(val taskId: String, val isCompleted: Boolean) : HomeAction
    data class DeleteTask(val task: Task) : HomeAction
    data object UndoDelete : HomeAction
    data class AddSubTask(val taskId: String, val subTaskTitle: String) : HomeAction
    data class ToggleSubTask(val taskId: String, val subTaskId: String, val isDone: Boolean) : HomeAction
    data class SetFilter(val filter: HomeFilter) : HomeAction
    // Tier 2: Error and state management actions
    data object RetryLoadTasks : HomeAction
    data object DismissError : HomeAction
    data object DismissSuccess : HomeAction
}
