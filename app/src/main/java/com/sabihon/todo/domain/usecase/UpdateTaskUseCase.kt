package com.sabihon.todo.domain.usecase

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case to update existing task.
 */
class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        if (task.id.isBlank()) return Result.Error(IllegalArgumentException("Task id required"))
        if (task.title.isBlank()) return Result.Error(IllegalArgumentException("Title cannot be empty"))
        return repository.updateTask(task)
    }
}
