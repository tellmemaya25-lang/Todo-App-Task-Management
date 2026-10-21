package com.sabihon.todo.domain.usecase

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case to delete task (soft delete for Undo).
 */
class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: String, softDelete: Boolean = true): Result<Unit> {
        if (taskId.isBlank()) return Result.Error(IllegalArgumentException("Task id required"))
        return repository.deleteTask(taskId, softDelete)
    }
}
