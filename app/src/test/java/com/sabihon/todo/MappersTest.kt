package com.sabihon.todo

import com.google.firebase.Timestamp
import com.sabihon.todo.data.remote.dto.CategoryDto
import com.sabihon.todo.data.remote.dto.SubTaskDto
import com.sabihon.todo.data.remote.dto.TaskDto
import com.sabihon.todo.data.remote.dto.generateSearchKeywords
import com.sabihon.todo.data.remote.dto.toDomain
import com.sabihon.todo.data.remote.dto.toDto
import com.sabihon.todo.domain.model.Priority
import com.sabihon.todo.domain.model.SubTask
import com.sabihon.todo.domain.model.Task
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class MappersTest {

    @Test
    fun `task dto to domain maps correctly`() {
        val dto = TaskDto(
            id = "123",
            title = "Test Task",
            description = "Desc",
            categoryId = "cat1",
            priority = "HIGH",
            dueAt = Timestamp(Date(1000L)),
            isCompleted = false,
            subTasks = listOf(SubTaskDto("s1", "Sub", false)),
            searchKeywords = listOf("test")
        )
        val domain = dto.toDomain()
        assertEquals("123", domain.id)
        assertEquals("Test Task", domain.title)
        assertEquals(Priority.HIGH, domain.priority)
        assertEquals(1, domain.subTasks.size)
        assertEquals("cat1", domain.categoryId)
    }

    @Test
    fun `task domain to dto generates search keywords`() {
        val task = Task(
            id = "123",
            title = "Buy Milk",
            priority = Priority.LOW
        )
        val dto = task.toDto()
        assertTrue(dto.searchKeywords.isNotEmpty())
        assertTrue(dto.searchKeywords.contains("buy"))
        assertEquals("LOW", dto.priority)
    }

    @Test
    fun `generateSearchKeywords creates prefixes`() {
        val keywords = generateSearchKeywords("Buy Milk")
        assertTrue(keywords.contains("buy"))
        assertTrue(keywords.contains("b"))
        assertTrue(keywords.contains("bu"))
        assertTrue(keywords.contains("milk"))
        assertTrue(keywords.contains("m"))
    }

    @Test
    fun `category dto to domain`() {
        val dto = CategoryDto(id = "c1", name = "Grocery", colorHex = "#D6E4FF", order = 0)
        val domain = dto.toDomain(taskCount = 5, completedCount = 2)
        assertEquals("c1", domain.id)
        assertEquals("Grocery", domain.name)
        assertEquals(5, domain.taskCount)
        assertEquals(2, domain.completedCount)
    }

    @Test
    fun `subtask mapping`() {
        val dto = SubTaskDto("id1", "Title", true)
        val domain = dto.toDomain()
        assertEquals("id1", domain.id)
        assertTrue(domain.isDone)
        val backToDto = domain.toDto()
        assertEquals(dto, backToDto)
    }
}
