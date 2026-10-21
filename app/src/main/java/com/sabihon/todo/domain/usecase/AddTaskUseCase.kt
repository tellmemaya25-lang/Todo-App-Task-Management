package com.sabihon.todo.domain.usecase

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Use case to add a new task.
 */
class AddTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<String> {
        if (task.title.isBlank()) {
            return Result.Error(IllegalArgumentException("Title cannot be empty"))
        }
        return repository.addTask(task)
    }
}
