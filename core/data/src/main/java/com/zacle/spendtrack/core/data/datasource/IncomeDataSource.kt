package com.zacle.spendtrack.core.data.datasource

import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow

interface IncomeDataSource {
    suspend fun getIncome(userId: String, incomeId: String): Flow<Income?>
    suspend fun getIncomes(userId: String, period: Period): Flow<List<Income>>
    suspend fun getIncomesByCategory(userId: String, categoryId: String, period: Period): Flow<List<Income>>
    suspend fun addIncome(income: Income)
    suspend fun updateIncome(income: Income)
    suspend fun deleteIncome(userId: String, incomeId: String)
}

interface SyncableIncomeDataSource : IncomeDataSource {
    suspend fun getNonSyncedIncomes(userId: String): List<Income>
}