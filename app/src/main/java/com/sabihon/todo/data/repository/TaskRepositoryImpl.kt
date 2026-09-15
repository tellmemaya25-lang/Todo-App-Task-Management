package com.sabihon.todo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sabihon.todo.core.util.Result
import com.sabihon.todo.data.remote.dto.ActivityDto
import com.sabihon.todo.data.remote.dto.TaskDto
import com.sabihon.todo.data.remote.dto.generateSearchKeywords
import com.sabihon.todo.data.remote.dto.toDomain
import com.sabihon.todo.data.remote.dto.toDto
import com.sabihon.todo.di.IoDispatcher
import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.model.TaskFilter
import com.sabihon.todo.domain.model.TaskStatusFilter
import com.sabihon.todo.domain.repository.TaskRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore implementation of TaskRepository with snapshot listeners.
 */
@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TaskRepository {

    private fun userIdOrThrow(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    private fun tasksCollection() =
        firestore.collection("users").document(userIdOrThrow()).collection("tasks")

    private fun activityCollection() =
        firestore.collection("users").document(userIdOrThrow()).collection("activity")

    override fun observeTasks(filter: TaskFilter): Flow<Result<List<Task>>> = callbackFlow {
        trySend(Result.Loading)
        try {
            var query: Query = tasksCollection()

            if (!filter.includeDeleted) {
                query = query.whereEqualTo("isDeleted", false)
            }
            when (filter.status) {
                TaskStatusFilter.PENDING -> query = query.whereEqualTo("isCompleted", false)
                TaskStatusFilter.COMPLETED -> query = query.whereEqualTo("isCompleted", true)
                else -> {}
            }
            filter.priority?.let {
                query = query.whereEqualTo("priority", it.name)
            }
            filter.categoryId?.let {
                query = query.whereEqualTo("categoryId", it)
            }

            // Sorting – we order by dueAt; for more complex sorting we sort in memory
            query = query.orderBy("dueAt", Query.Direction.ASCENDING)

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val tasks = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(TaskDto::class.java)?.toDomain()
                        } catch (e: Exception) {
                            null
                        }
                    }.let { list ->
                        // Apply additional in-memory filtering for dueDateRange if needed
                        applyInMemoryFilters(list, filter)
                    }
                    trySend(Result.Success(tasks))
                }
            }
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            trySend(Result.Error(e))
        }
    }

    private fun applyInMemoryFilters(tasks: List<Task>, filter: TaskFilter): List<Task> {
        var result = tasks
        filter.dueDateRange?.let { range ->
            val now = System.currentTimeMillis()
            val startOfDay = getStartOfDay(now)
            val endOfDay = getEndOfDay(now)
            result = when (range) {
                com.sabihon.todo.domain.model.DueDateRange.TODAY ->
                    result.filter { it.dueAt != null && it.dueAt in startOfDay..endOfDay }
                com.sabihon.todo.domain.model.DueDateRange.OVERDUE ->
                    result.filter { it.dueAt != null && it.dueAt < now && !it.isCompleted }
                com.sabihon.todo.domain.model.DueDateRange.THIS_WEEK ->
                    result.filter { it.dueAt != null && it.dueAt in startOfDay..(startOfDay + 7 * 24 * 60 * 60 * 1000L) }
                else -> result
            }
        }
        filter.searchQuery?.let { q ->
            if (q.isNotBlank()) {
                val lower = q.lowercase()
                result = result.filter { it.title.lowercase().contains(lower) || it.description.lowercase().contains(lower) }
            }
        }
        // Sort
        result = when (filter.sortBy) {
            com.sabihon.todo.domain.model.SortBy.PRIORITY -> result.sortedBy { it.priority.ordinal }
            com.sabihon.todo.domain.model.SortBy.CREATED_AT -> result.sortedBy { it.createdAt }
            com.sabihon.todo.domain.model.SortBy.TITLE -> result.sortedBy { it.title.lowercase() }
            else -> result.sortedBy { it.dueAt ?: Long.MAX_VALUE }
        }
        if (!filter.sortAscending) result = result.reversed()
        return result
    }

    override fun observeTaskById(taskId: String): Flow<Result<Task>> = callbackFlow {
        trySend(Result.Loading)
        val docRef = tasksCollection().document(taskId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.Error(error))
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val dto = snapshot.toObject(TaskDto::class.java)
                if (dto != null) {
                    trySend(Result.Success(dto.toDomain()))
                } else {
                    trySend(Result.Error(Exception("Task not found")))
                }
            } else {
                trySend(Result.Error(Exception("Task not found")))
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addTask(task: Task): Result<String> = withContext(ioDispatcher) {
        try {
            val uid = userIdOrThrow()
            val dto = task.copy(
                searchKeywords = if (task.searchKeywords.isEmpty()) generateSearchKeywords(task.title) else task.searchKeywords,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ).toDto()
            val docRef = if (task.id.isBlank()) tasksCollection().document() else tasksCollection().document(task.id)
            val finalDto = dto.copy(id = docRef.id)
            docRef.set(finalDto).await()
            // Activity log
            logActivity(taskId = docRef.id, taskTitle = task.title, action = "CREATED")
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> = withContext(ioDispatcher) {
        try {
            val dto = task.copy(
                updatedAt = System.currentTimeMillis(),
                searchKeywords = if (task.searchKeywords.isEmpty()) generateSearchKeywords(task.title) else task.searchKeywords
            ).toDto()
            tasksCollection().document(task.id).set(dto).await()
            logActivity(taskId = task.id, taskTitle = task.title, action = "UPDATED")
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteTask(taskId: String, softDelete: Boolean): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (softDelete) {
                tasksCollection().document(taskId).update(
                    mapOf(
                        "isDeleted" to true,
                        "updatedAt" to Timestamp(Date())
                    )
                ).await()
            } else {
                tasksCollection().document(taskId).delete().await()
            }
            logActivity(taskId = taskId, taskTitle = "", action = "DELETED")
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun restoreTask(taskId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            tasksCollection().document(taskId).update(
                mapOf(
                    "isDeleted" to false,
                    "updatedAt" to Timestamp(Date())
                )
            ).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun toggleComplete(taskId: String, isCompleted: Boolean): Result<Unit> = withContext(ioDispatcher) {
        try {
            val updates = mutableMapOf<String, Any?>(
                "isCompleted" to isCompleted,
                "updatedAt" to Timestamp(Date())
            )
            if (isCompleted) {
                updates["completedAt"] = Timestamp(Date())
            } else {
                updates["completedAt"] = null
            }
            tasksCollection().document(taskId).update(updates).await()
            logActivity(
                taskId = taskId,
                taskTitle = "",
                action = if (isCompleted) "COMPLETED" else "REOPENED"
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun searchTasks(query: String): Flow<Result<List<Task>>> = flow {
        emit(Result.Loading)
        try {
            if (query.isBlank()) {
                emit(Result.Success(emptyList()))
                return@flow
            }
            val lower = query.lowercase()
            // Firestore prefix search using searchKeywords array-contains
            val snapshot = tasksCollection()
                .whereEqualTo("isDeleted", false)
                .whereArrayContains("searchKeywords", lower)
                .get()
                .await()
            val tasks = snapshot.documents.mapNotNull { it.toObject(TaskDto::class.java)?.toDomain() }
            emit(Result.Success(tasks))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    private suspend fun logActivity(taskId: String, taskTitle: String, action: String) {
        try {
            val dto = ActivityDto(
                taskId = taskId,
                taskTitle = taskTitle,
                action = action,
                timestamp = Timestamp(Date())
            )
            activityCollection().add(dto).await()
        } catch (_: Exception) {
            // Ignore activity log failures
        }
    }

    private fun getStartOfDay(timeMillis: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = timeMillis
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getEndOfDay(timeMillis: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = timeMillis
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
        cal.set(java.util.Calendar.MINUTE, 59)
        cal.set(java.util.Calendar.SECOND, 59)
        cal.set(java.util.Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}
