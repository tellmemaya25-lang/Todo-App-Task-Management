package com.sabihon.todo.domain.model

/**
 * Core Task domain model – immutable.
 */
data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val categoryId: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val dueAt: Long? = null, // epoch millis
    val reminderAt: Long? = null,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val subTasks: List<SubTask> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val searchKeywords: List<String> = emptyList()
)
