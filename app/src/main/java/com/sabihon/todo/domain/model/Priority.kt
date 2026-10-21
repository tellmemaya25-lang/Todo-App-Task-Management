package com.sabihon.todo.domain.model

/**
 * Priority levels for tasks.
 */
enum class Priority {
    LOW, MEDIUM, HIGH, URGENT;

    companion object {
        fun fromString(value: String?): Priority {
            return when (value?.uppercase()) {
                "LOW" -> LOW
                "MEDIUM" -> MEDIUM
                "HIGH" -> HIGH
                "URGENT" -> URGENT
                else -> MEDIUM
            }
        }
    }
}
