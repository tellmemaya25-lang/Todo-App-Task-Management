package com.sabihon.todo.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Activity log DTO – users/{uid}/activity/{activityId}
 */
data class ActivityDto(
    @DocumentId
    val id: String = "",
    val taskId: String = "",
    val taskTitle: String = "",
    val action: String = "CREATED", // CREATED|UPDATED|COMPLETED|REOPENED|DELETED
    @ServerTimestamp
    val timestamp: Timestamp? = null
)
