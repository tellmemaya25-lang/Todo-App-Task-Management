package com.sabihon.todo

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.model.Task
import com.sabihon.todo.domain.repository.TaskRepository
import com.sabihon.todo.domain.usecase.UpdateTaskUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class UpdateTaskUseCaseTest {
    private val repo = mockk<TaskRepository>()
    private val useCase = UpdateTaskUseCase(repo)

    @Test
    fun `update succeeds with valid data`() = runTest {
        val task = Task(id = "1", title = "Updated")
        coEvery { repo.updateTask(any()) } returns Result.Success(Unit)
        val result = useCase(task)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `update fails when id blank`() = runTest {
        val task = Task(id = "", title = "Title")
        val result = useCase(task)
        assertTrue(result is Result.Error)
    }

    @Test
    fun `update fails when title blank`() = runTest {
        val task = Task(id = "1", title = "")
        val result = useCase(task)
        assertTrue(result is Result.Error)
    }
}
