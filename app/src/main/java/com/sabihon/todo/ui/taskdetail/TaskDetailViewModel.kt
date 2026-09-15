package com.sabihon.todo.ui.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.model.SubTask
import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.repository.TaskRepository
import com.sabihon.todo.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailUiState(
    val isLoading: Boolean = true,
    val task: Task? = null,
    val error: String? = null
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            repository.observeTaskById(taskId).collectLatest { result ->
                when (result) {
                    is Result.Success -> _uiState.update { it.copy(isLoading = false, task = result.data) }
                    is Result.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun toggleSubTask(subTaskId: String) {
        viewModelScope.launch {
            val currentTask = _uiState.value.task ?: return@launch
            val updatedSubs = currentTask.subTasks.map {
                if (it.id == subTaskId) it.copy(isDone = !it.isDone) else it
            }
            val updatedTask = currentTask.copy(subTasks = updatedSubs)
            updateTaskUseCase(updatedTask)
        }
    }

    fun addSubTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val currentTask = _uiState.value.task ?: return@launch
            val newSub = SubTask(
                id = System.currentTimeMillis().toString(),
                title = title,
                isDone = false
            )
            val updatedTask = currentTask.copy(subTasks = currentTask.subTasks + newSub)
            updateTaskUseCase(updatedTask)
        }
    }

    fun updateDescription(newDesc: String) {
        viewModelScope.launch {
            val currentTask = _uiState.value.task ?: return@launch
            if (currentTask.description == newDesc) return@launch
            val updatedTask = currentTask.copy(description = newDesc)
            updateTaskUseCase(updatedTask)
        }
    }

    fun updateTitle(newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            val currentTask = _uiState.value.task ?: return@launch
            if (currentTask.title == newTitle) return@launch
            val updatedTask = currentTask.copy(title = newTitle)
            updateTaskUseCase(updatedTask)
        }
    }
}
