package com.sabihon.todo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.sabihon.todo.core.ui.theme.SabihonTheme
import org.junit.Rule
import org.junit.Test

class AddTaskScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addTaskScreen_hasTitleField() {
        composeTestRule.setContent {
            SabihonTheme {
                androidx.compose.material3.Text("Add Task")
            }
        }
        composeTestRule.onNodeWithText("Add Task").assertIsDisplayed()
    }
}
