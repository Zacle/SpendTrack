package com.zacle.spendtrack.core.domain.repository

import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {
    suspend fun getIncome(userId: String, incomeId: String): Flow<Income?>
    suspend fun getIncomes(userId: String, period: Period): Flow<List<Income>>
    suspend fun getIncomesByCategory(userId: String, categoryId: String, period: Period): Flow<List<Income>>
    suspend fun addIncome(userId: String, income: Income)
    suspend fun updateIncome(userId: String, income: Income)
    suspend fun deleteIncome(userId: String, incomeId: String)
}