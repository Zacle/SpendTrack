package com.zacle.spendtrack.core.data.datasource

import com.zacle.spendtrack.core.model.DeletedIncome

interface DeletedIncomeDataSource {
    suspend fun insert(deletedIncome: DeletedIncome)
    suspend fun delete(userId: String, incomeId: String)
    suspend fun getDeletedIncomes(userId: String): List<DeletedIncome>
}