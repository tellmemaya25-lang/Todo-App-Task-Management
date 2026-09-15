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
import com.sabihon.todo.notifications.ReminderScheduler
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
 * Firestore implementation – fixed field name mapping (completed/deleted not isCompleted/isDeleted)
 * and robust query handling without composite index requirement.
 */
@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val reminderScheduler: ReminderScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TaskRepository {

    private fun userIdOrThrow(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated – please login again")

    private fun tasksCollection() =
        firestore.collection("users").document(userIdOrThrow()).collection("tasks")

    private fun activityCollection() =
        firestore.collection("users").document(userIdOrThrow()).collection("activity")

    override fun observeTasks(filter: TaskFilter): Flow<Result<List<Task>>> = callbackFlow {
        trySend(Result.Loading)
        try {
            val uid = try { userIdOrThrow() } catch (e: Exception) {
                trySend(Result.Error(e, "Not authenticated"))
                close()
                return@callbackFlow
            }
            android.util.Log.d("TaskRepo", "observeTasks uid=$uid filter=$filter")

            // Robust approach: only filter by deleted in Firestore to avoid composite index requirement.
            // All other filtering (status, priority, category, search) done in-memory.
            // This ensures tasks show even if indexes missing.
            val baseQuery: Query = if (!filter.includeDeleted) {
                tasksCollection().whereEqualTo("deleted", false)
            } else {
                tasksCollection()
            }

            // Try with orderBy, fallback without if fails
            val queryWithOrder = try {
                baseQuery.orderBy("dueAt", Query.Direction.ASCENDING)
            } catch (e: Exception) {
                android.util.Log.w("TaskRepo", "orderBy failed, using unordered", e)
                baseQuery
            }

            val listener = queryWithOrder.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("TaskRepo", "Snapshot error: ${error.message}", error)
                    val msg = error.message ?: ""
                    if (msg.contains("PERMISSION_DENIED", ignoreCase = true)) {
                        trySend(Result.Error(error, "Firestore permission denied. Deploy firestore.rules. See FIRESTORE_FIX.md"))
                    } else if (msg.contains("index", ignoreCase = true) || msg.contains("FAILED_PRECONDITION")) {
                        // Fallback to simplest query: just collection without filters
                        android.util.Log.w("TaskRepo", "Index missing, fallback to simple collection")
                        try {
                            tasksCollection().addSnapshotListener { snap2, err2 ->
                                if (err2 != null) {
                                    trySend(Result.Error(err2, err2.message))
                                    return@addSnapshotListener
                                }
                                val tasks = snap2?.documents?.mapNotNull { doc ->
                                    try { doc.toObject(TaskDto::class.java)?.toDomain() } catch (e: Exception) {
                                        android.util.Log.e("TaskRepo", "Parse error ${doc.id}", e); null
                                    }
                                }?.let { applyInMemoryFilters(it, filter) } ?: emptyList()
                                android.util.Log.d("TaskRepo", "Fallback snapshot ${tasks.size} tasks")
                                trySend(Result.Success(tasks))
                            }
                        } catch (e: Exception) {
                            trySend(Result.Error(e))
                        }
                    } else {
                        trySend(Result.Error(error, error.message))
                    }
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    android.util.Log.d("TaskRepo", "Snapshot ${snapshot.size()} docs for uid=$uid")
                    val tasks = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(TaskDto::class.java)?.toDomain()
                        } catch (e: Exception) {
                            android.util.Log.e("TaskRepo", "Failed parse ${doc.id}: ${e.message}", e)
                            null
                        }
                    }.let { applyInMemoryFilters(it, filter) }
                    android.util.Log.d("TaskRepo", "After filter ${tasks.size} tasks")
                    trySend(Result.Success(tasks))
                }
            }
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            android.util.Log.e("TaskRepo", "observeTasks crash", e)
            trySend(Result.Error(e, e.message ?: "Failed to observe"))
        }
    }

    private fun applyInMemoryFilters(tasks: List<Task>, filter: TaskFilter): List<Task> {
        var result = tasks

        // IncludeDeleted already handled in query, but double-check
        if (!filter.includeDeleted) {
            result = result.filter { !it.isDeleted }
        }

        // Status filter in-memory to avoid composite index
        when (filter.status) {
            TaskStatusFilter.PENDING -> result = result.filter { !it.isCompleted }
            TaskStatusFilter.COMPLETED -> result = result.filter { it.isCompleted }
            else -> {}
        }

        filter.priority?.let { pri ->
            result = result.filter { it.priority == pri }
        }
        filter.categoryId?.let { catId ->
            result = result.filter { it.categoryId == catId }
        }

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
        try {
            val docRef = tasksCollection().document(taskId)
            val listener = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error, error.message))
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    try {
                        val dto = snapshot.toObject(TaskDto::class.java)
                        if (dto != null) trySend(Result.Success(dto.toDomain()))
                        else trySend(Result.Error(Exception("Task not found")))
                    } catch (e: Exception) {
                        trySend(Result.Error(e))
                    }
                } else {
                    trySend(Result.Error(Exception("Task not found")))
                }
            }
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            trySend(Result.Error(e))
        }
    }

    override suspend fun addTask(task: Task): Result<String> = withContext(ioDispatcher) {
        try {
            val uid = userIdOrThrow()
            android.util.Log.d("TaskRepo", "addTask uid=$uid title=${task.title}")

            val dto = task.copy(
                searchKeywords = if (task.searchKeywords.isEmpty()) generateSearchKeywords(task.title) else task.searchKeywords,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ).toDto()

            val docRef = if (task.id.isBlank()) tasksCollection().document() else tasksCollection().document(task.id)
            val finalDto = dto.copy(id = docRef.id)

            android.util.Log.d("TaskRepo", "Writing users/$uid/tasks/${docRef.id}")

            try {
                docRef.set(finalDto).await()
            } catch (e: Exception) {
                android.util.Log.e("TaskRepo", "Full DTO failed, fallback minimal", e)
                val minimal = hashMapOf(
                    "title" to task.title,
                    "description" to task.description,
                    "categoryId" to task.categoryId,
                    "priority" to task.priority.name,
                    "dueAt" to task.dueAt?.let { Timestamp(Date(it)) },
                    "reminderAt" to task.reminderAt?.let { Timestamp(Date(it)) },
                    "completed" to false,
                    "deleted" to false,
                    "searchKeywords" to generateSearchKeywords(task.title),
                    "createdAt" to Timestamp(Date()),
                    "updatedAt" to Timestamp(Date()),
                    "subTasks" to emptyList<Map<String, Any>>()
                )
                docRef.set(minimal).await()
            }

            android.util.Log.d("TaskRepo", "Task written ${docRef.id}")

            try { logActivity(taskId = docRef.id, taskTitle = task.title, action = "CREATED") } catch (e: Exception) {
                android.util.Log.w("TaskRepo", "logActivity failed", e)
            }
            try { task.reminderAt?.let { reminderScheduler.scheduleReminder(docRef.id, task.title, task.description, it) } } catch (e: Exception) {
                android.util.Log.e("TaskRepo", "Reminder failed", e)
            }

            Result.Success(docRef.id)
        } catch (e: Exception) {
            android.util.Log.e("TaskRepo", "addTask FAILED ${e.message}", e)
            val msg = e.message ?: "Failed"
            val friendly = when {
                msg.contains("PERMISSION_DENIED", true) -> "Firestore permission denied. Deploy firestore.rules"
                msg.contains("UNAUTHENTICATED", true) || msg.contains("not authenticated", true) -> "Not authenticated. Logout/login."
                msg.contains("UNAVAILABLE", true) -> "Firestore unavailable – check internet"
                else -> msg
            }
            Result.Error(e, friendly)
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
            try {
                if (task.reminderAt != null) reminderScheduler.scheduleReminder(task.id, task.title, task.description, task.reminderAt)
                else reminderScheduler.cancelReminder(task.id)
            } catch (e: Exception) { android.util.Log.e("TaskRepo", "Reminder reschedule failed", e) }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed update")
        }
    }

    override suspend fun deleteTask(taskId: String, softDelete: Boolean): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (softDelete) {
                tasksCollection().document(taskId).update(mapOf("deleted" to true, "updatedAt" to Timestamp(Date()))).await()
            } else {
                tasksCollection().document(taskId).delete().await()
            }
            logActivity(taskId = taskId, taskTitle = "", action = "DELETED")
            try { reminderScheduler.cancelReminder(taskId) } catch (_: Exception) {}
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun restoreTask(taskId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            tasksCollection().document(taskId).update(mapOf("deleted" to false, "updatedAt" to Timestamp(Date()))).await()
            Result.Success(Unit)
        } catch (e: Exception) { Result.Error(e) }
    }

    override suspend fun toggleComplete(taskId: String, isCompleted: Boolean): Result<Unit> = withContext(ioDispatcher) {
        try {
            val updates = mutableMapOf<String, Any?>("completed" to isCompleted, "updatedAt" to Timestamp(Date()))
            if (isCompleted) updates["completedAt"] = Timestamp(Date()) else updates["completedAt"] = null
            tasksCollection().document(taskId).update(updates).await()
            logActivity(taskId = taskId, taskTitle = "", action = if (isCompleted) "COMPLETED" else "REOPENED")
            if (isCompleted) try { reminderScheduler.cancelReminder(taskId) } catch (_: Exception) {}
            Result.Success(Unit)
        } catch (e: Exception) { Result.Error(e) }
    }

    override suspend fun searchTasks(query: String): Flow<Result<List<Task>>> = flow {
        emit(Result.Loading)
        try {
            if (query.isBlank()) { emit(Result.Success(emptyList())); return@flow }
            val lower = query.lowercase()
            val snapshot = try {
                tasksCollection().whereEqualTo("deleted", false).whereArrayContains("searchKeywords", lower).get().await()
            } catch (e: Exception) {
                android.util.Log.w("TaskRepo", "search index missing, fallback", e)
                tasksCollection().whereEqualTo("deleted", false).get().await()
            }
            val tasks = snapshot.documents.mapNotNull {
                try { it.toObject(TaskDto::class.java)?.toDomain() } catch (_: Exception) { null }
            }.filter { it.title.lowercase().contains(lower) || it.description.lowercase().contains(lower) }
            emit(Result.Success(tasks))
        } catch (e: Exception) { emit(Result.Error(e)) }
    }

    private suspend fun logActivity(taskId: String, taskTitle: String, action: String) {
        try {
            val dto = ActivityDto(taskId = taskId, taskTitle = taskTitle, action = action, timestamp = Timestamp(Date()))
            activityCollection().add(dto).await()
        } catch (_: Exception) {}
    }

    private fun getStartOfDay(timeMillis: Long): Long {
        val cal = java.util.Calendar.getInstance().apply {
            this.timeInMillis = timeMillis
            set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getEndOfDay(timeMillis: Long): Long {
        val cal = java.util.Calendar.getInstance().apply {
            this.timeInMillis = timeMillis
            set(java.util.Calendar.HOUR_OF_DAY, 23); set(java.util.Calendar.MINUTE, 59)
            set(java.util.Calendar.SECOND, 59); set(java.util.Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }
}
