package com.zacle.spendtrack.core.data.datasource

import com.zacle.spendtrack.core.model.DeletedBudget

interface DeletedBudgetDataSource {
    suspend fun insert(deletedBudget: DeletedBudget)
    suspend fun delete(userId: String, budgetId: String)
    suspend fun getDeletedBudgets(userId: String): List<DeletedBudget>
}