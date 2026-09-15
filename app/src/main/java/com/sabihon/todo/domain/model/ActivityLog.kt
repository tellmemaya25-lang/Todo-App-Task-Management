package com.sabihon.todo.domain.model

/**
 * Activity log entry for task history.
 */
data class ActivityLog(
    val id: String = "",
    val taskId: String = "",
    val taskTitle: String = "",
    val action: ActivityAction = ActivityAction.CREATED,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ActivityAction {
    CREATED, UPDATED, COMPLETED, REOPENED, DELETED
}
