package com.sabihon.todo.ui.categories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CategoriesScreen(
    onBack: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {}
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Categories – Loop 6")
    }
}

@Composable
fun CategoryDetailScreen(
    categoryId: String,
    onBack: () -> Unit = {},
    onTaskClick: (String) -> Unit = {},
    onAddTask: () -> Unit = {}
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Category Detail $categoryId – Loop 6")
    }
}
