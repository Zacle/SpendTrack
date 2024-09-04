package com.zacle.spendtrack.core.domain.repository

import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import com.zacle.spendtrack.core.model.util.Syncable
import kotlinx.coroutines.flow.Flow

interface BudgetRepository: Syncable {
    suspend fun getBudgets(userId: String, budgetPeriod: Period): Flow<List<Budget>>
    suspend fun getBudget(userId: String, budgetId: String): Flow<Budget?>
    suspend fun addBudget(budget: Budget)
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
}