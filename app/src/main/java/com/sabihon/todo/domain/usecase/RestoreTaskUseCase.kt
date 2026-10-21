package com.sabihon.todo.domain.usecase

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.repository.TaskRepository
import javax.inject.Inject

class RestoreTaskUseCase @Inject constructor(private val repo: TaskRepository) {
    suspend operator fun invoke(taskId: String): Result<Unit> = repo.restoreTask(taskId)
}
