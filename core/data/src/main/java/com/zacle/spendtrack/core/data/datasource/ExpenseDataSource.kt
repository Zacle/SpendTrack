package com.zacle.spendtrack.core.data.datasource

import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow

interface ExpenseDataSource {
    suspend fun getExpense(userId: String, expenseId: String): Flow<Expense?>
    suspend fun getExpenses(userId: String, period: Period): Flow<List<Expense>>
    suspend fun getExpensesByCategory(userId: String, categoryId: String, period: Period): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense)
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
}