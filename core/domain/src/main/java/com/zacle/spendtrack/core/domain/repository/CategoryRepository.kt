package com.zacle.spendtrack.core.domain.repository

import com.zacle.spendtrack.core.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun categories(): Flow<List<Category>>
}