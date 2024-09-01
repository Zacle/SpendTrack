package com.zacle.spendtrack.core.data.repository

import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.common.di.LocalExpenseData
import com.zacle.spendtrack.core.common.di.RemoteExpenseData
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.datasource.ExpenseDataSource
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Offline-first implementation of [ExpenseRepository] that uses both a network and an offline storage
 * source. When a expense is first requested, it is requested from the local database, and if empty,
 * we check if the user is online. If they are, we request the expense from the network and save it
 * in the local database.
 *
 * Add, Update and Delete Expense first in the local database. Then if the user is online, we do the
 * same in the network. If the user is offline, we schedule a background work using [WorkManager]
 */
class OfflineFirstExpenseRepository @Inject constructor(
    @LocalExpenseData private val localExpenseDataSource: ExpenseDataSource,
    @RemoteExpenseData private val remoteExpenseDataSource: ExpenseDataSource,
    @STDispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkMonitor: NetworkMonitor
): ExpenseRepository {
    override suspend fun getExpenses(userId: String, period: Period): Flow<List<Expense>> =
        localExpenseDataSource.getExpenses(userId, period).flatMapLatest { expenses ->
            val isOnline = networkMonitor.isOnline.first()
            if (expenses.isEmpty() && isOnline) {
                val remoteExpenses = remoteExpenseDataSource.getExpenses(userId, period).first()
                remoteExpenses.forEach { expense ->
                    localExpenseDataSource.addExpense(expense)
                }
                flow { emit(remoteExpenses) }
            } else {
                flow { emit(expenses) }
            }
        }.flowOn(ioDispatcher)

    override suspend fun getExpensesByCategory(
        userId: String,
        categoryId: String,
        period: Period
    ): Flow<List<Expense>> = localExpenseDataSource.getExpensesByCategory(userId, categoryId, period)
        .flatMapLatest { expenses ->
            val isOnline = networkMonitor.isOnline.first()
            if (expenses.isEmpty() && isOnline) {
                val remoteExpenses = remoteExpenseDataSource.getExpensesByCategory(userId, categoryId, period).first()
                remoteExpenses.forEach { expense ->
                    localExpenseDataSource.addExpense(expense)
                }
                flow { emit(remoteExpenses) }
            } else {
                flow { emit(expenses) }
            }
        }.flowOn(ioDispatcher)

    override suspend fun getExpense(userId: String, expenseId: String): Flow<Expense?> =
        localExpenseDataSource.getExpense(userId, expenseId).flatMapLatest { expense ->
            val isOnline = networkMonitor.isOnline.first()
            if (expense == null && isOnline) {
                val remoteExpense = remoteExpenseDataSource.getExpense(userId, expenseId).first()
                if (remoteExpense != null) {
                    localExpenseDataSource.addExpense(remoteExpense)
                }
                flow { emit(remoteExpense) }
            } else {
                flow { emit(expense) }
            }
        }

    override suspend fun addExpense(expense: Expense) {
        localExpenseDataSource.addExpense(expense)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteExpenseDataSource.addExpense(expense)
        } else {
            // TODO: Handle offline case
        }
    }

    override suspend fun updateExpense(expense: Expense) {
        localExpenseDataSource.updateExpense(expense)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteExpenseDataSource.updateExpense(expense)
        } else {
            // TODO: Handle offline case
        }
    }

    override suspend fun deleteExpense(expense: Expense) {
        localExpenseDataSource.deleteExpense(expense)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteExpenseDataSource.deleteExpense(expense)
        } else {
            // TODO: Handle offline case
        }
    }
}