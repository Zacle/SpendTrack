package com.zacle.spendtrack.core.domain.repository

import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Period
import com.zacle.spendtrack.core.model.util.Syncable
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository: Syncable {
    suspend fun getExpenses(userId: String, period: Period): Flow<List<Expense>>
    suspend fun getExpensesByCategory(userId: String, categoryId: String, period: Period): Flow<List<Expense>>
    suspend fun getExpense(userId: String, expenseId: String): Flow<Expense?>
    suspend fun addExpense(expense: Expense)
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
}