package com.sabihon.todo.domain.usecase

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Toggle task completion.
 */
class ToggleCompleteUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: String, isCompleted: Boolean): Result<Unit> {
        return repository.toggleComplete(taskId, isCompleted)
    }
}
