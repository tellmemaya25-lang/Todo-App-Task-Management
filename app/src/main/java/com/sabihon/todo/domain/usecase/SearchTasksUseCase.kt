package com.sabihon.todo.domain.usecase

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Search tasks via prefix search on searchKeywords.
 */
class SearchTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(query: String): Flow<Result<List<Task>>> {
        return repository.searchTasks(query)
    }
}
