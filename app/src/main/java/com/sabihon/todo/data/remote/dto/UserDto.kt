package com.sabihon.todo.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

/**
 * Firestore DTO for User – users/{uid}
 */
data class UserDto(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val themePref: String = "SYSTEM", // SYSTEM | LIGHT | DARK
    val fcmToken: String? = null
)
