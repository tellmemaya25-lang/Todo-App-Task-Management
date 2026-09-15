package com.sabihon.todo.ui.search

import com.sabihon.todo.domain.model.Category
import com.sabihon.todo.domain.model.Priority
import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.model.TaskFilter
import com.sabihon.todo.domain.model.TaskStatusFilter
import com.sabihon.todo.domain.model.SortBy

data class SearchUiState(
    val query: String = "",
    val filter: TaskFilter = TaskFilter(),
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val recentSearches: List<String> = emptyList(),
    val categories: List<Category> = emptyList(),
    val error: String? = null
)
