package com.sabihon.todo.ui.home

import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.usecase.DashboardCounts

/**
 * Immutable UiState for Home Dashboard.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val greetingName: String = "User",
    val counts: DashboardCounts = DashboardCounts(),
    val todayTasks: List<Task> = emptyList(),
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
}
