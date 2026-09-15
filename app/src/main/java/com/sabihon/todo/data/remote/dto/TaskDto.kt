package com.sabihon.todo.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Firestore DTO for Task – mirrors users/{uid}/tasks/{taskId}
 */
data class TaskDto(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val categoryId: String? = null,
    val priority: String = "MEDIUM", // LOW | MEDIUM | HIGH | URGENT
    val dueAt: Timestamp? = null,
    val reminderAt: Timestamp? = null,
    val isCompleted: Boolean = false,
    val completedAt: Timestamp? = null,
    val subTasks: List<SubTaskDto> = emptyList(),
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    val isDeleted: Boolean = false,
    val searchKeywords: List<String> = emptyList()
)

data class SubTaskDto(
    val id: String = "",
    val title: String = "",
    val isDone: Boolean = false
)
