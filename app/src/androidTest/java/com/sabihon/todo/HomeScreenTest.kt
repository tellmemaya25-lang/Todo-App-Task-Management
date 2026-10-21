package com.sabihon.todo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.sabihon.todo.core.ui.components.PastelStatTile
import com.sabihon.todo.core.ui.theme.SabihonTheme
import com.sabihon.todo.core.ui.theme.SoftBlue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun statTile_renders() {
        composeTestRule.setContent {
            SabihonTheme {
                PastelStatTile(
                    label = "Today",
                    count = 5,
                    backgroundColor = SoftBlue,
                    icon = androidx.compose.material.icons.Icons.Default.AccessTime
                )
            }
        }
        composeTestRule.onNodeWithText("Today").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
    }
}
