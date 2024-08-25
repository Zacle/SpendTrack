package com.zacle.spendtrack.core.data.datasource

import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow

interface BudgetDataSource {
    suspend fun getBudget(userId: String, budgetId: String, budgetPeriod: Period): Flow<Budget?>
    suspend fun getBudgets(userId: String, budgetPeriod: Period): Flow<List<Budget>>
    suspend fun addBudget(budget: Budget)
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
}