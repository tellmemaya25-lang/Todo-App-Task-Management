package com.sabihon.todo.ui.addedit

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sabihon.todo.core.ui.components.ConfirmDialog
import com.sabihon.todo.core.ui.components.PriorityChip
import com.sabihon.todo.domain.model.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Add/Edit Task – full screen with all fields.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTaskScreen(
    taskId: String?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Edit Task" else "Add Task") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    if (uiState.isEditMode) {
                        IconButton(onClick = { viewModel.setShowDeleteConfirm(true) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                isError = uiState.titleError != null,
                supportingText = { uiState.titleError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Category picker
            Text("Category", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.categoryId == null,
                    onClick = { viewModel.onCategorySelected(null) },
                    label = { Text("Uncategorized") }
                )
                uiState.categories.forEach { cat ->
                    FilterChip(
                        selected = uiState.categoryId == cat.id,
                        onClick = { viewModel.onCategorySelected(cat.id) },
                        label = { Text(cat.name) }
                    )
                }
            }

            // Priority
            Text("Priority", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriorityChip(priority = Priority.LOW, selected = uiState.priority == Priority.LOW, onClick = { viewModel.onPrioritySelected(Priority.LOW) })
                PriorityChip(priority = Priority.MEDIUM, selected = uiState.priority == Priority.MEDIUM, onClick = { viewModel.onPrioritySelected(Priority.MEDIUM) })
                PriorityChip(priority = Priority.HIGH, selected = uiState.priority == Priority.HIGH, onClick = { viewModel.onPrioritySelected(Priority.HIGH) })
                PriorityChip(priority = Priority.URGENT, selected = uiState.priority == Priority.URGENT, onClick = { viewModel.onPrioritySelected(Priority.URGENT) })
            }

            // Due date & time
            Text("Due Date & Time", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = { showDatePicker = true }) {
                    Text(
                        text = uiState.dueAt?.let { formatDate(it) } ?: "Select Date"
                    )
                }
                TextButton(onClick = { showTimePicker = true }, enabled = uiState.dueAt != null || uiState.dueDateMillis != null) {
                    Text(
                        text = uiState.dueAt?.let { formatTime(it) } ?: "Select Time"
                    )
                }
            }

            // Reminder toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Reminder", style = MaterialTheme.typography.titleMedium)
                Switch(checked = uiState.reminderEnabled, onCheckedChange = viewModel::onReminderToggle)
            }

            // Sub-tasks
            Text("Sub-tasks", style = MaterialTheme.typography.titleMedium)
            uiState.subTasks.forEach { sub ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = sub.isDone, onCheckedChange = { viewModel.toggleSubTask(sub.id) })
                    Text(text = sub.title, modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.removeSubTask(sub.id) }) {
                        Icon(Icons.Filled.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = uiState.newSubTaskTitle,
                    onValueChange = viewModel::onNewSubTaskTitleChange,
                    label = { Text("New sub-task") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(Modifier.size(8.dp))
                Button(onClick = { viewModel.addSubTask() }) {
                    Text("Add")
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { viewModel.saveTask(onSaved) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Text(if (uiState.isEditMode) "Update" else "Save")
            }

            uiState.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.dueAt ?: System.currentTimeMillis())
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.onDueDateSelected(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            var timeState = rememberTimePickerState(is24Hour = false)
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.onDueTimeSelected(timeState.hour, timeState.minute)
                        showTimePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                },
                text = {
                    TimePicker(state = timeState)
                }
            )
        }

        if (uiState.showDeleteConfirm) {
            ConfirmDialog(
                title = "Delete Task",
                message = "Are you sure you want to delete this task? This cannot be undone.",
                confirmText = "Delete",
                onConfirm = {
                    viewModel.setShowDeleteConfirm(false)
                    viewModel.deleteTask(onSaved)
                },
                onDismiss = { viewModel.setShowDeleteConfirm(false) }
            )
        }
    }
}

private fun formatDate(millis: Long): String {
    val fmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return fmt.format(Date(millis))
}

private fun formatTime(millis: Long): String {
    val fmt = SimpleDateFormat("h:mm a", Locale.getDefault())
    return fmt.format(Date(millis))
}
