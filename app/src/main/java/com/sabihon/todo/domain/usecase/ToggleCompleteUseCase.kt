package com.sabihon.todo.domain.usecase

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Toggle task completion – with guard: dont mark as complete when all sub-tasks are not done
 * Per user request: task should only be completable when all sub-tasks are done
 */
class ToggleCompleteUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: String, isCompleted: Boolean): Result<Unit> {
        if (isCompleted) {
            try {
                val flowResult = repository.observeTaskById(taskId).first()
                if (flowResult is Result.Success) {
                    val task = flowResult.data
                    if (task.subTasks.isNotEmpty() && task.subTasks.any { !it.isDone }) {
                        // Don't mark as complete when all tasks are not done
                        return Result.Error("Complete all ${task.subTasks.size} sub-tasks first (${task.subTasks.count { it.isDone }}/${task.subTasks.size} done)")
                    }
                }
            } catch (e: Exception) {
                // If we can't fetch, allow toggle to proceed – fallback
            }
        }
        return repository.toggleComplete(taskId, isCompleted)
    }
}
