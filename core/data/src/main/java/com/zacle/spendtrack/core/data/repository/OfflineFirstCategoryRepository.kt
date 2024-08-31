package com.zacle.spendtrack.core.data.repository

import com.zacle.spendtrack.core.data.datasource.CategoryDataSource
import com.zacle.spendtrack.core.domain.repository.CategoryRepository
import com.zacle.spendtrack.core.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineFirstCategoryRepository @Inject constructor(
    private val categoryDataSource: CategoryDataSource
): CategoryRepository {
    override suspend fun categories(): Flow<List<Category>> = categoryDataSource.getCategories()
}