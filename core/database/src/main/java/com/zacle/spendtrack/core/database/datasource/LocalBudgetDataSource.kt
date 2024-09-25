package com.zacle.spendtrack.core.database.datasource

import com.zacle.spendtrack.core.data.datasource.SyncableBudgetDataSource
import com.zacle.spendtrack.core.database.dao.BudgetDao
import com.zacle.spendtrack.core.database.model.asEntity
import com.zacle.spendtrack.core.database.model.asExternalModel
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

/**
 * [SyncableBudgetDataSource] implementation based on a Room
 */
class LocalBudgetDataSource @Inject constructor(
    private val budgetDao: BudgetDao
): SyncableBudgetDataSource {
    override suspend fun getBudget(
        userId: String,
        budgetId: String
    ): Flow<Budget?> =
        budgetDao
            .getBudget(
                userId = userId,
                budgetId = budgetId
            )
            .mapLatest { it?.asExternalModel() }


    override suspend fun getBudgets(userId: String, budgetPeriod: Period): Flow<List<Budget>> =
        budgetDao
            .getBudgets(
                userId = userId,
                start = budgetPeriod.start.toEpochMilliseconds(),
                end = budgetPeriod.end.toEpochMilliseconds()
            )
            .mapLatest { budgets -> budgets.map { it.asExternalModel() } }

    override suspend fun addAllBudgets(budgets: List<Budget>) {
        budgetDao.insertAllBudgets(budgets.map { it.asEntity() })
    }


    override suspend fun addBudget(budget: Budget) {
        budgetDao.insertBudget(budget.asEntity())
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget.asEntity())
    }

    override suspend fun deleteBudget(userId: String, budgetId: String) {
        budgetDao.deleteBudget(userId, budgetId)
    }

    override suspend fun getNonSyncedBudgets(userId: String): List<Budget> =
        budgetDao.getNonSyncedBudgets(userId).map { it.asExternalModel() }
}