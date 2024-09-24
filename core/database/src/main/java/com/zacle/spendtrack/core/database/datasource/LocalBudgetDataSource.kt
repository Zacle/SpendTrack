package com.zacle.spendtrack.core.database.datasource

import com.zacle.spendtrack.core.data.datasource.SyncableBudgetDataSource
import com.zacle.spendtrack.core.database.dao.BudgetDao
import com.zacle.spendtrack.core.database.model.asEntity
import com.zacle.spendtrack.core.database.model.asExternalModel
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

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


    override suspend fun addBudget(budget: Budget) {
        try {
            Timber.d("Adding budget = ${budget.category.key}")

            // Ensure the insert is non-cancellable if that's the desired behavior
            withContext(NonCancellable) {
                budgetDao.insertBudget(budget.asEntity())
            }

            Timber.d("Budget added successfully = ${budget.category.key}")
        } catch (e: CancellationException) {
            // Handle coroutine cancellation specifically
            Timber.e("Coroutine was cancelled while adding budget = ${budget.category.key}")
            throw e // Re-throw cancellation exceptions
        } catch (e: Exception) {
            Timber.e(e, "Error adding budget = ${budget.category.key}")
        }
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