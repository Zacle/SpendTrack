package com.zacle.spendtrack.core.data.datasource

import com.zacle.spendtrack.core.model.DeletedExpense

interface DeletedExpenseDataSource {
    suspend fun insert(deletedExpense: DeletedExpense)
    suspend fun delete(userId: String, expenseId: String)
    suspend fun getDeletedExpenses(userId: String): List<DeletedExpense>
}