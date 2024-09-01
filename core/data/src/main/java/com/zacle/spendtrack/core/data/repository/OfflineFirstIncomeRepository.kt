package com.zacle.spendtrack.core.data.repository

import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.common.di.LocalIncomeData
import com.zacle.spendtrack.core.common.di.RemoteIncomeData
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.datasource.IncomeDataSource
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Offline-first implementation of [IncomeRepository] that uses both a network and an offline storage
 * source. When a income is first requested, it is requested from the local database, and if empty,
 * we check if the user is online. If they are, we request the income from the network and save it
 * in the local database.
 *
 * Add, Update and Delete Income first in the local database. Then if the user is online, we do the
 * same in the network. If the user is offline, we schedule a background work using [WorkManager]
 */
class OfflineFirstIncomeRepository @Inject constructor(
    @LocalIncomeData private val localIncomeDataSource: IncomeDataSource,
    @RemoteIncomeData private val remoteIncomeDataSource: IncomeDataSource,
    @STDispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkMonitor: NetworkMonitor
): IncomeRepository {
    override suspend fun getIncomes(userId: String, period: Period): Flow<List<Income>> =
        localIncomeDataSource.getIncomes(userId, period).flatMapLatest { incomes ->
            val isOnline = networkMonitor.isOnline.first()
            if (incomes.isEmpty() && isOnline) {
                val remoteIncomes = remoteIncomeDataSource.getIncomes(userId, period).first()
                remoteIncomes.forEach { income ->
                    localIncomeDataSource.addIncome(income)
                }
                flow { emit(remoteIncomes) }
            } else {
                flow { emit(incomes) }
            }
        }.flowOn(ioDispatcher)

    override suspend fun getIncomesByCategory(
        userId: String,
        categoryId: String,
        period: Period
    ): Flow<List<Income>> = localIncomeDataSource.getIncomesByCategory(userId, categoryId, period)
        .flatMapLatest { incomes ->
            val isOnline = networkMonitor.isOnline.first()
            if (incomes.isEmpty() && isOnline) {
                val remoteIncomes = remoteIncomeDataSource.getIncomesByCategory(userId, categoryId, period).first()
                remoteIncomes.forEach { income ->
                    localIncomeDataSource.addIncome(income)
                }
                flow { emit(remoteIncomes) }
            } else {
                flow { emit(incomes) }
            }
        }.flowOn(ioDispatcher)

    override suspend fun getIncome(userId: String, incomeId: String): Flow<Income?> =
        localIncomeDataSource.getIncome(userId, incomeId).flatMapLatest { income ->
            val isOnline = networkMonitor.isOnline.first()
            if (income == null && isOnline) {
                val remoteIncome = remoteIncomeDataSource.getIncome(userId, incomeId).first()
                if (remoteIncome != null) {
                    localIncomeDataSource.addIncome(remoteIncome)
                }
                flow { emit(remoteIncome) }
            } else {
                flow { emit(income) }
            }
        }

    override suspend fun addIncome(income: Income) {
        localIncomeDataSource.addIncome(income)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteIncomeDataSource.addIncome(income)
        } else {
            // TODO: Handle offline case
        }
    }

    override suspend fun updateIncome(income: Income) {
        localIncomeDataSource.updateIncome(income)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteIncomeDataSource.updateIncome(income)
        } else {
            // TODO: Handle offline case
        }
    }

    override suspend fun deleteIncome(income: Income) {
        localIncomeDataSource.deleteIncome(income)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteIncomeDataSource.deleteIncome(income)
        } else {
            // TODO: Handle offline case
        }
    }
}