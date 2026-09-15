package com.sabihon.todo.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sabihon.todo.R
import com.sabihon.todo.core.ui.components.TaskRow
import com.sabihon.todo.core.ui.theme.AccentBlue
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
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.query.isNotBlank()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(Modifier.height(12.dp))

            // Recent searches suggestions – reduced to important ones as requested
            if (uiState.query.isBlank() && uiState.recentSearches.isNotEmpty()) {
                Text("Recent searches", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column {
                        // Reduced to 2 most important recent searches
                        uiState.recentSearches.take(2).forEach { recent ->
                            ListItem(
                                headlineContent = { Text(recent) },
                                leadingContent = { Icon(Icons.Filled.History, contentDescription = null) },
                                modifier = Modifier.clickable {
                                    viewModel.onQueryChange(recent)
                                    viewModel.saveRecentSearch(recent)
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            } else if (uiState.query.isNotBlank() && uiState.recentSearches.isNotEmpty()) {
                // Show only 1-2 filtered suggestions based on query – reduced
                val suggestions = uiState.recentSearches.filter { it.contains(uiState.query, ignoreCase = true) }.take(2)
                if (suggestions.isNotEmpty()) {
                    Text("Suggestions", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        suggestions.forEach { suggestion ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    viewModel.onQueryChange(suggestion)
                                    viewModel.saveRecentSearch(suggestion)
                                },
                                label = { Text(suggestion) },
                                leadingIcon = { Icon(Icons.Filled.History, contentDescription = null) }
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // Filter chips – blue highlights per request (was green)
            Text("Filters", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.filter.status == TaskStatusFilter.ALL,
                    onClick = { viewModel.onStatusFilterChange(TaskStatusFilter.ALL) },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )
                FilterChip(
                    selected = uiState.filter.status == TaskStatusFilter.PENDING,
                    onClick = { viewModel.onStatusFilterChange(TaskStatusFilter.PENDING) },
                    label = { Text("Pending") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = uiState.filter.status == TaskStatusFilter.COMPLETED,
                    onClick = { viewModel.onStatusFilterChange(TaskStatusFilter.COMPLETED) },
                    label = { Text("Completed") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue,
                        selectedLabelColor = Color.White
                    )
                )
                Priority.values().forEach { pri ->
                    FilterChip(
                        selected = uiState.filter.priority == pri,
                        onClick = {
                            if (uiState.filter.priority == pri) viewModel.onPriorityFilterChange(null)
                            else viewModel.onPriorityFilterChange(pri)
                        },
                        label = { Text(pri.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.filter.categoryId == null,
                    onClick = { viewModel.onCategoryFilterChange(null) },
                    label = { Text("All Categories") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue,
                        selectedLabelColor = Color.White
                    )
                )
                uiState.categories.forEach { cat ->
                    FilterChip(
                        selected = uiState.filter.categoryId == cat.id,
                        onClick = { viewModel.onCategoryFilterChange(cat.id) },
                        label = { Text(cat.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.filter.sortBy == SortBy.DUE_DATE,
                    onClick = { viewModel.onSortChange(SortBy.DUE_DATE, true) },
                    label = { Text("Sort by Due Date") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = uiState.filter.sortBy == SortBy.PRIORITY,
                    onClick = { viewModel.onSortChange(SortBy.PRIORITY, false) },
                    label = { Text("Sort by Priority") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(Modifier.height(12.dp))
            Text("${uiState.tasks.size} results", style = MaterialTheme.typography.labelMedium)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (uiState.tasks.isEmpty() && uiState.query.isNotBlank()) {
                    item {
                        // No search results illustration – uses provided image
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp, bottom = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.no_search_illustration),
                                contentDescription = "No results",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                                    .padding(horizontal = 16.dp)
                            )
                            Text(
                                text = "No results found",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Try adjusting your search or filters\nWe couldn't find any tasks matching '${uiState.query}'",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            TextButton(
                                onClick = { viewModel.onQueryChange("") },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Clear search", color = AccentBlue)
                            }
                        }
                    }
                } else {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskRow(
                            title = task.title,
                            isCompleted = task.isCompleted,
                            timeLabel = task.dueAt?.let { java.text.SimpleDateFormat("MMM d • h:mm a").format(java.util.Date(it)) } ?: "No date",
                            description = task.description,
                            onClick = {
                                viewModel.saveRecentSearch(task.title)
                                onTaskClick(task.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
