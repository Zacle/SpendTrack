package com.zacle.spendtrack.core.data.repository

import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.common.di.LocalBudgetData
import com.zacle.spendtrack.core.common.di.RemoteBudgetData
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.datasource.BudgetDataSource
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Offline-first implementation of [BudgetRepository] that uses both a network and an offline storage
 * source. When a budget is first requested, it is requested from the local database, and if empty,
 * we check if the user is online. If they are, we request the budget from the network and save it
 * in the local database.
 *
 * Add, Update and Delete Budget first in the local database. Then if the user is online, we do the
 * same in the network. If the user is offline, we schedule a background work using [WorkManager]
 */
class OfflineFirstBudgetRepository @Inject constructor(
    @LocalBudgetData private val localBudgetDataSource: BudgetDataSource,
    @RemoteBudgetData private val remoteBudgetDataSource: BudgetDataSource,
    @STDispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkMonitor: NetworkMonitor
): BudgetRepository {
    override suspend fun getBudgets(userId: String, budgetPeriod: Period): Flow<List<Budget>> =
        localBudgetDataSource.getBudgets(userId, budgetPeriod).flatMapLatest { budgets ->
            val isOnline = networkMonitor.isOnline.first()
            if (budgets.isEmpty() && isOnline) {
                val remoteBudgets = remoteBudgetDataSource.getBudgets(userId, budgetPeriod).first()
                remoteBudgets.forEach { budget ->
                    localBudgetDataSource.addBudget(budget)
                }
                flow { emit(remoteBudgets) }
            } else {
                flow { emit(budgets) }
            }
        }.flowOn(ioDispatcher)

    override suspend fun getBudget(
        userId: String,
        budgetId: String,
        budgetPeriod: Period
    ): Flow<Budget?> = localBudgetDataSource.getBudget(userId, budgetId, budgetPeriod)
        .flatMapLatest { budget ->
            val isOnline = networkMonitor.isOnline.first()
            if (budget == null && isOnline) {
                val remoteBudget = remoteBudgetDataSource.getBudget(userId, budgetId, budgetPeriod).first()
                if (remoteBudget != null) {
                    localBudgetDataSource.addBudget(remoteBudget)
                }
                flow { emit(remoteBudget) }
            } else {
                flow { emit(budget) }
            }
        }

    override suspend fun addBudget(budget: Budget) {
        localBudgetDataSource.addBudget(budget)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteBudgetDataSource.addBudget(budget)
        } else {
            // TODO: Handle offline case
        }
    }

    override suspend fun updateBudget(budget: Budget) {
        localBudgetDataSource.updateBudget(budget)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteBudgetDataSource.updateBudget(budget)
        } else {
            // TODO: Handle offline case
        }
    }

    override suspend fun deleteBudget(budget: Budget) {
        localBudgetDataSource.deleteBudget(budget)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteBudgetDataSource.deleteBudget(budget)
        } else {
            // TODO: Handle offline case
        }
    }
}