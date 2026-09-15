package com.sabihon.todo.ui.taskdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TaskDetailScreen(
    taskId: String,
    onBack: () -> Unit = {},
    onEdit: (String) -> Unit = {}
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Task Detail $taskId – Loop 5")
    }
}
