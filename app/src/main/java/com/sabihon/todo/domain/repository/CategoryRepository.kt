package com.sabihon.todo.domain.repository

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Category repository contract.
 */
interface CategoryRepository {
    fun observeCategories(): Flow<Result<List<Category>>>
    suspend fun addCategory(category: Category): Result<String>
    suspend fun updateCategory(category: Category): Result<Unit>
    suspend fun deleteCategory(categoryId: String): Result<Unit>
    suspend fun seedDefaultCategories(): Result<Unit>
}
