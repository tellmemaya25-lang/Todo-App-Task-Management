package com.sabihon.todo.ui.addedit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AddEditTaskScreen(
    taskId: String?,
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Add/Edit Task ${taskId ?: "new"} – Loop 5")
    }
}
