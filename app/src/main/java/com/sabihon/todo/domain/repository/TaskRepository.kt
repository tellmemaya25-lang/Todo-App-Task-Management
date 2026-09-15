package com.sabihon.todo.domain.repository

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.model.TaskFilter
import kotlinx.coroutines.flow.Flow

/**
 * Task repository contract.
 */
interface TaskRepository {
    fun observeTasks(filter: TaskFilter = TaskFilter()): Flow<Result<List<Task>>>
    fun observeTaskById(taskId: String): Flow<Result<Task>>
    suspend fun addTask(task: Task): Result<String>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(taskId: String, softDelete: Boolean = true): Result<Unit>
    suspend fun restoreTask(taskId: String): Result<Unit>
    suspend fun toggleComplete(taskId: String, isCompleted: Boolean): Result<Unit>
    suspend fun searchTasks(query: String): Flow<Result<List<Task>>>
}
