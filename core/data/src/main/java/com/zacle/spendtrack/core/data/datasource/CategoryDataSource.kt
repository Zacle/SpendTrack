package com.zacle.spendtrack.core.data.datasource

import com.zacle.spendtrack.core.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryDataSource {
    suspend fun getCategories(): Flow<List<Category>>
}