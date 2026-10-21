package com.sabihon.todo

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.repository.TaskRepository
import com.sabihon.todo.domain.usecase.ToggleCompleteUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ToggleCompleteUseCaseTest {
    private val repo = mockk<TaskRepository>()
    private val useCase = ToggleCompleteUseCase(repo)

    @Test
    fun `toggle complete success`() = runTest {
        coEvery { repo.toggleComplete("1", true) } returns Result.Success(Unit)
        val result = useCase("1", true)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `toggle complete error`() = runTest {
        coEvery { repo.toggleComplete("1", false) } returns Result.Error(Exception("fail"))
        val result = useCase("1", false)
        assertTrue(result is Result.Error)
    }
}
