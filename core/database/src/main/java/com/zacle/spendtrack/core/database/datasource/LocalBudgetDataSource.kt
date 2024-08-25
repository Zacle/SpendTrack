package com.zacle.spendtrack.core.database.datasource

import com.zacle.spendtrack.core.data.datasource.BudgetDataSource
import com.zacle.spendtrack.core.database.dao.BudgetDao
import com.zacle.spendtrack.core.database.model.asEntity
import com.zacle.spendtrack.core.database.model.asExternalModel
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class LocalBudgetDataSource @Inject constructor(
    private val budgetDao: BudgetDao
): BudgetDataSource {
    override suspend fun getBudget(
        userId: String,
        budgetId: String,
        budgetPeriod: Period
    ): Flow<Budget?> =
        budgetDao
            .getBudget(
                userId = userId,
                budgetId = budgetId,
                start = budgetPeriod.start.toEpochMilliseconds(),
                end = budgetPeriod.end.toEpochMilliseconds()
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


    override suspend fun addBudget(budget: Budget) {
        budgetDao.insertBudget(budget.asEntity())
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget.asEntity())
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget.asEntity())
    }
}