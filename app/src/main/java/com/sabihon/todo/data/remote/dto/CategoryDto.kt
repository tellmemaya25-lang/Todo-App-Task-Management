package com.sabihon.todo.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Firestore DTO for Category – users/{uid}/categories/{categoryId}
 */
data class CategoryDto(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val colorHex: String = "#D6E4FF",
    val iconKey: String = "folder",
    val order: Int = 0,
    @ServerTimestamp
    val createdAt: Timestamp? = null
)
