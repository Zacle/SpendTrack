package com.zacle.spendtrack.core.domain.repository

import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun getExpense(userId: String, expenseId: String): Flow<Expense?>
    suspend fun getExpenses(userId: String, period: Period): Flow<List<Expense>>
    suspend fun getExpensesByCategory(userId: String, categoryId: String, period: Period): Flow<List<Expense>>
    suspend fun addExpense(userId: String, expense: Expense)
    suspend fun updateExpense(userId: String, expense: Expense)
    suspend fun deleteExpense(userId: String, expense: Expense)
}