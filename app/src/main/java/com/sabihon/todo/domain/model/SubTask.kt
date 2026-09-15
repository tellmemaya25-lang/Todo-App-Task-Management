package com.sabihon.todo.domain.model

/**
 * Sub-task model – nested within a Task.
 */
data class SubTask(
    val id: String = "",
    val title: String = "",
    val isDone: Boolean = false
)
