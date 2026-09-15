package com.sabihon.todo.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sabihon.todo.core.ui.components.EmptyState
import com.sabihon.todo.core.ui.components.TaskRow
import com.sabihon.todo.domain.model.Priority
import com.sabihon.todo.domain.model.SortBy
import com.sabihon.todo.domain.model.TaskStatusFilter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit = {},
    onTaskClick: (String) -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("Search tasks…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.query.isNotBlank()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            // Filter chips
            Text("Filters", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.filter.status == TaskStatusFilter.ALL,
                    onClick = { viewModel.onStatusFilterChange(TaskStatusFilter.ALL) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = uiState.filter.status == TaskStatusFilter.PENDING,
                    onClick = { viewModel.onStatusFilterChange(TaskStatusFilter.PENDING) },
                    label = { Text("Pending") }
                )
                FilterChip(
                    selected = uiState.filter.status == TaskStatusFilter.COMPLETED,
                    onClick = { viewModel.onStatusFilterChange(TaskStatusFilter.COMPLETED) },
                    label = { Text("Completed") }
                )
                Priority.values().forEach { pri ->
                    FilterChip(
                        selected = uiState.filter.priority == pri,
                        onClick = {
                            if (uiState.filter.priority == pri) viewModel.onPriorityFilterChange(null)
                            else viewModel.onPriorityFilterChange(pri)
                        },
                        label = { Text(pri.name) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.filter.categoryId == null,
                    onClick = { viewModel.onCategoryFilterChange(null) },
                    label = { Text("All Categories") }
                )
                uiState.categories.forEach { cat ->
                    FilterChip(
                        selected = uiState.filter.categoryId == cat.id,
                        onClick = { viewModel.onCategoryFilterChange(cat.id) },
                        label = { Text(cat.name) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.filter.sortBy == SortBy.DUE_DATE,
                    onClick = { viewModel.onSortChange(SortBy.DUE_DATE, true) },
                    label = { Text("Sort by Due Date") }
                )
                FilterChip(
                    selected = uiState.filter.sortBy == SortBy.PRIORITY,
                    onClick = { viewModel.onSortChange(SortBy.PRIORITY, false) },
                    label = { Text("Sort by Priority") }
                )
            }

            Spacer(Modifier.height(12.dp))
            Text("${uiState.tasks.size} results", style = MaterialTheme.typography.labelMedium)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (uiState.tasks.isEmpty()) {
                    item {
                        EmptyState(
                            title = "No results found",
                            description = "Try adjusting your search or filters"
                        )
                    }
                } else {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskRow(
                            title = task.title,
                            isCompleted = task.isCompleted,
                            timeLabel = task.dueAt?.let { java.text.SimpleDateFormat("MMM d • h:mm a").format(java.util.Date(it)) } ?: "No date",
                            description = task.description,
                            onClick = { onTaskClick(task.id) }
                        )
                    }
                }
            }
        }
    }
}
