package com.sabihon.todo.data.repository

import com.sabihon.todo.core.util.Result
import com.sabihon.todo.domain.model.Category
import com.sabihon.todo.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Placeholder CategoryRepository – full implementation in Loop 2.
 */
@Singleton
class CategoryRepositoryImpl @Inject constructor() : CategoryRepository {
    override fun observeCategories(): Flow<Result<List<Category>>> = flowOf(Result.Success(emptyList()))
    override suspend fun addCategory(category: Category): Result<String> = Result.Error(Exception("Not implemented"))
    override suspend fun updateCategory(category: Category): Result<Unit> = Result.Error(Exception("Not implemented"))
    override suspend fun deleteCategory(categoryId: String): Result<Unit> = Result.Error(Exception("Not implemented"))
    override suspend fun seedDefaultCategories(): Result<Unit> = Result.Success(Unit)
}
