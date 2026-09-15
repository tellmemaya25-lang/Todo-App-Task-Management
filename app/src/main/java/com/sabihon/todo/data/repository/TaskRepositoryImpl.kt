package com.sabihon.todo.data.repository

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.model.TaskFilter
import com.sabihon.todo.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Placeholder TaskRepository – full Firestore implementation in Loop 2.
 */
@Singleton
class TaskRepositoryImpl @Inject constructor() : TaskRepository {
    override fun observeTasks(filter: TaskFilter): Flow<Result<List<Task>>> = flowOf(Result.Success(emptyList()))
    override fun observeTaskById(taskId: String): Flow<Result<Task>> = flowOf(Result.Error(Exception("Not implemented")))
    override suspend fun addTask(task: Task): Result<String> = Result.Error(Exception("Not implemented"))
    override suspend fun updateTask(task: Task): Result<Unit> = Result.Error(Exception("Not implemented"))
    override suspend fun deleteTask(taskId: String, softDelete: Boolean): Result<Unit> = Result.Error(Exception("Not implemented"))
    override suspend fun restoreTask(taskId: String): Result<Unit> = Result.Error(Exception("Not implemented"))
    override suspend fun toggleComplete(taskId: String, isCompleted: Boolean): Result<Unit> = Result.Error(Exception("Not implemented"))
    override suspend fun searchTasks(query: String): Flow<Result<List<Task>>> = flowOf(Result.Success(emptyList()))
}
