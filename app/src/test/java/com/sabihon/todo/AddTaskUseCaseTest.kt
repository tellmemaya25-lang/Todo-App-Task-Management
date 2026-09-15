package com.sabihon.todo

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.repository.TaskRepository
import com.sabihon.todo.domain.usecase.AddTaskUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class AddTaskUseCaseTest {

    private val repository = mockk<TaskRepository>()
    private val useCase = AddTaskUseCase(repository)

    @Test
    fun `add task succeeds`() = runTest {
        val task = Task(title = "Test")
        coEvery { repository.addTask(any()) } returns Result.Success("id123")
        val result = useCase(task)
        assertTrue(result is Result.Success)
        assertEquals("id123", (result as Result.Success).data)
    }

    @Test
    fun `add task fails when title blank`() = runTest {
        val task = Task(title = "")
        val result = useCase(task)
        assertTrue(result is Result.Error)
    }

    @Test
    fun `add task propagates repository error`() = runTest {
        val task = Task(title = "Valid")
        coEvery { repository.addTask(any()) } returns Result.Error(Exception("Network error"))
        val result = useCase(task)
        assertTrue(result is Result.Error)
    }
}
