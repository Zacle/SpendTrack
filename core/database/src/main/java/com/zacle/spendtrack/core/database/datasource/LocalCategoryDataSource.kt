package com.zacle.spendtrack.core.database.datasource

import com.zacle.spendtrack.core.data.datasource.CategoryDataSource
import com.zacle.spendtrack.core.database.dao.CategoryDao
import com.zacle.spendtrack.core.database.model.asEntity
import com.zacle.spendtrack.core.database.model.asExternalModel
import com.zacle.spendtrack.core.model.Category
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class LocalCategoryDataSource @Inject constructor(
    private val categoryDao: CategoryDao
): CategoryDataSource {
    override suspend fun getCategories() =
        categoryDao.getAllCategories().mapLatest { categories ->
            categories.map { it.asExternalModel() }
        }

    override suspend fun insertAllCategories(categories: List<Category>) {
        categoryDao.insertAll(categories.map { it.asEntity() })
    }
}