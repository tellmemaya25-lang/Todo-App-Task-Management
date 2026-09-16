package com.sabihon.todo.domain.model

/**
 * Category / list grouping for tasks.
 */
data class Category(
    val id: String = "",
    val name: String = "",
    val colorHex: String = "#D6E4FF",
    val iconKey: String = "folder",
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val taskCount: Int = 0,
    val completedCount: Int = 0
)
