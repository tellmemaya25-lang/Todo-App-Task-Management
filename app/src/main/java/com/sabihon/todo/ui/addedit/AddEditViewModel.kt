package com.sabihon.todo.ui.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.model.Category
import com.sabihon.todo.domain.model.Priority
import com.sabihon.todo.domain.model.SubTask
import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.repository.CategoryRepository
import com.sabihon.todo.domain.repository.TaskRepository
import com.sabihon.todo.domain.usecase.AddTaskUseCase
import com.sabihon.todo.domain.usecase.DeleteTaskUseCase
import com.sabihon.todo.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for Add/Edit Task – handles optimistic updates.
 */
@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        observeCategories()
    }

    private fun observeCategories() {
        viewModelScope.launch {
            categoryRepository.observeCategories().collectLatest { result ->
                if (result is Result.Success) {
                    _uiState.update { it.copy(categories = result.data) }
                }
            }
        }
    }

    fun loadTask(taskId: String?) {
        if (taskId == null) {
            _uiState.update { it.copy(isEditMode = false) }
            return
        }
        _uiState.update { it.copy(isLoading = true, taskId = taskId, isEditMode = true) }
        viewModelScope.launch {
            taskRepository.observeTaskById(taskId).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        val task = result.data
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                title = task.title,
                                description = task.description,
                                categoryId = task.categoryId,
                                priority = task.priority,
                                dueAt = task.dueAt,
                                reminderEnabled = task.reminderAt != null,
                                reminderAt = task.reminderAt,
                                subTasks = task.subTasks
                            )
                        }
                    }
                    is Result.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun onDescriptionChange(desc: String) {
        _uiState.update { it.copy(description = desc) }
    }

    fun onCategorySelected(categoryId: String?) {
        _uiState.update { it.copy(categoryId = categoryId) }
    }

    fun onPrioritySelected(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun onDueDateSelected(millis: Long?) {
        _uiState.update { current ->
            val newDueAt = combineDateAndTime(millis, current.dueTimeMillis ?: current.dueAt)
            current.copy(dueDateMillis = millis, dueAt = newDueAt)
        }
    }

    fun onDueTimeSelected(hour: Int, minute: Int) {
        _uiState.update { current ->
            val cal = Calendar.getInstance()
            current.dueDateMillis?.let { cal.timeInMillis = it } ?: current.dueAt?.let { cal.timeInMillis = it }
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val timeMillis = cal.timeInMillis
            val combined = combineDateAndTime(current.dueDateMillis ?: current.dueAt, timeMillis)
            current.copy(dueTimeMillis = timeMillis, dueAt = combined)
        }
    }

    private fun combineDateAndTime(dateMillis: Long?, timeMillis: Long?): Long? {
        if (dateMillis == null && timeMillis == null) return null
        val calDate = Calendar.getInstance()
        val calTime = Calendar.getInstance()
        if (dateMillis != null) calDate.timeInMillis = dateMillis
        if (timeMillis != null) calTime.timeInMillis = timeMillis

        val result = Calendar.getInstance()
        if (dateMillis != null) {
            result.set(Calendar.YEAR, calDate.get(Calendar.YEAR))
            result.set(Calendar.MONTH, calDate.get(Calendar.MONTH))
            result.set(Calendar.DAY_OF_MONTH, calDate.get(Calendar.DAY_OF_MONTH))
        }
        if (timeMillis != null) {
            result.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY))
            result.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE))
        } else {
            // Default time 9 AM if only date selected
            result.set(Calendar.HOUR_OF_DAY, 9)
            result.set(Calendar.MINUTE, 0)
        }
        result.set(Calendar.SECOND, 0)
        result.set(Calendar.MILLISECOND, 0)
        return result.timeInMillis
    }

    fun onReminderToggle(enabled: Boolean) {
        _uiState.update {
            it.copy(
                reminderEnabled = enabled,
                reminderAt = if (enabled) it.dueAt else null
            )
        }
    }

    fun onNewSubTaskTitleChange(title: String) {
        _uiState.update { it.copy(newSubTaskTitle = title) }
    }

    fun addSubTask() {
        val title = _uiState.value.newSubTaskTitle.trim()
        if (title.isBlank()) return
        val newSub = SubTask(id = System.currentTimeMillis().toString(), title = title, isDone = false)
        _uiState.update { it.copy(subTasks = it.subTasks + newSub, newSubTaskTitle = "") }
    }

    fun removeSubTask(subTaskId: String) {
        _uiState.update { it.copy(subTasks = it.subTasks.filter { st -> st.id != subTaskId }) }
    }

    fun toggleSubTask(subTaskId: String) {
        _uiState.update { current ->
            val updated = current.subTasks.map { if (it.id == subTaskId) it.copy(isDone = !it.isDone) else it }
            current.copy(subTasks = updated)
        }
    }

    fun saveTask(onSuccess: () -> Unit) {
        if (_uiState.value.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val state = _uiState.value
            val task = Task(
                id = state.taskId ?: "",
                title = state.title.trim(),
                description = state.description.trim(),
                categoryId = state.categoryId,
                priority = state.priority,
                dueAt = state.dueAt,
                reminderAt = if (state.reminderEnabled) state.reminderAt ?: state.dueAt else null,
                subTasks = state.subTasks,
                isCompleted = false
            )

            val result = if (state.isEditMode) {
                updateTaskUseCase(task)
            } else {
                addTaskUseCase(task).let { res ->
                    when (res) {
                        is Result.Success -> Result.Success(Unit)
                        is Result.Error -> res
                        else -> Result.Error(Exception("Unknown"))
                    }
                }
            }

            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                }
                else -> _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun deleteTask(onSuccess: () -> Unit) {
        val taskId = _uiState.value.taskId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            when (val result = deleteTaskUseCase(taskId, softDelete = false)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess()
                }
                is Result.Error -> _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                else -> {}
            }
        }
    }

    fun setShowDeleteConfirm(show: Boolean) {
        _uiState.update { it.copy(showDeleteConfirm = show) }
    }
}
