package com.zacle.spendtrack.core.database.datasource

import com.zacle.spendtrack.core.data.datasource.SyncableExpenseDataSource
import com.zacle.spendtrack.core.database.dao.ExpenseDao
import com.zacle.spendtrack.core.database.model.asEntity
import com.zacle.spendtrack.core.database.model.asExternalModel
import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * [SyncableExpenseDataSource] implementation based on a Room
 */
class LocalExpenseDataSource @Inject constructor(
    private val expenseDao: ExpenseDao
): SyncableExpenseDataSource {
    override suspend fun getExpense(userId: String, expenseId: String): Flow<Expense?> =
        expenseDao.getExpense(userId, expenseId).mapLatest { it?.asExternalModel() }

    override suspend fun getExpenses(userId: String, period: Period): Flow<List<Expense>> =
        expenseDao
            .getExpenses(userId, period.start.toEpochMilliseconds(), period.end.toEpochMilliseconds())
            .mapLatest { expenses -> expenses.map { it.asExternalModel() } }

    override suspend fun getExpensesByCategory(
        userId: String,
        categoryId: String,
        period: Period
    ): Flow<List<Expense>> =
        expenseDao
            .getExpensesByCategory(
                userId = userId,
                categoryId = categoryId,
                start = period.start.toEpochMilliseconds(),
                end = period.end.toEpochMilliseconds()
            )
            .mapLatest { expenses -> expenses.map { it.asExternalModel() } }

    override suspend fun addExpense(expense: Expense) {
        try {
            Timber.d("Adding expense = ${expense.name}")

            // Ensure the insert is non-cancellable if that's the desired behavior
            withContext(NonCancellable) {
                expenseDao.insertExpense(expense.asEntity())
            }

            Timber.d("Expense added successfully = ${expense.name}")
        } catch (e: CancellationException) {
            // Handle coroutine cancellation specifically
            Timber.e("Coroutine was cancelled while adding expense = ${expense.name}")
            throw e // Re-throw cancellation exceptions
        } catch (e: Exception) {
            Timber.e(e, "Error adding expense = ${expense.name}")
        }
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.asEntity())
    }

    override suspend fun deleteExpense(userId: String, expenseId: String) {
        expenseDao.deleteExpense(userId, expenseId)
    }

    override suspend fun getNonSyncedExpenses(userId: String): List<Expense> =
        expenseDao.getNonSyncedExpenses(userId).map { it.asExternalModel() }
}