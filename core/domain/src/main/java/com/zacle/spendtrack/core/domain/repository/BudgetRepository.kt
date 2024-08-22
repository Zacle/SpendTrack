package com.zacle.spendtrack.core.domain.repository

import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    suspend fun getBudget(userId: String, budgetId: String, budgetPeriod: Period): Flow<Budget?>
    suspend fun getBudgets(userId: String, budgetPeriod: Period): Flow<List<Budget>>
    suspend fun addBudget(userId: String, budget: Budget)
    suspend fun updateBudget(userId: String, budget: Budget)
    suspend fun deleteBudget(userId: String, budgetId: String)
}