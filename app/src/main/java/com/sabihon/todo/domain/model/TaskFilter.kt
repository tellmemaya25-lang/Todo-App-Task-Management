package com.sabihon.todo.domain.model

/**
 * Filter criteria for task queries.
 */
data class TaskFilter(
    val status: TaskStatusFilter = TaskStatusFilter.ALL,
    val priority: Priority? = null,
    val categoryId: String? = null,
    val dueDateRange: DueDateRange? = null,
    val searchQuery: String? = null,
    val sortBy: SortBy = SortBy.DUE_DATE,
    val sortAscending: Boolean = true,
    val includeDeleted: Boolean = false
)

enum class TaskStatusFilter { ALL, PENDING, COMPLETED }
enum class DueDateRange { TODAY, THIS_WEEK, OVERDUE, CUSTOM }
enum class SortBy { DUE_DATE, PRIORITY, CREATED_AT, TITLE }
