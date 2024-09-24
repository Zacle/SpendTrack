package com.zacle.spendtrack.core.database.datasource

import com.zacle.spendtrack.core.data.datasource.SyncableIncomeDataSource
import com.zacle.spendtrack.core.database.dao.IncomeDao
import com.zacle.spendtrack.core.database.model.asEntity
import com.zacle.spendtrack.core.database.model.asExternalModel
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * [SyncableIncomeDataSource] implementation for Room
 */
class LocalIncomeDataSource @Inject constructor(
    private val incomeDao: IncomeDao
): SyncableIncomeDataSource {
    override suspend fun getIncome(userId: String, incomeId: String): Flow<Income?> =
        incomeDao
            .getIncome(
                userId = userId,
                incomeId = incomeId
            )
            .mapLatest { it?.asExternalModel() }

    override suspend fun getIncomes(userId: String, period: Period): Flow<List<Income>> =
        incomeDao
            .getIncomes(
                userId = userId,
                start = period.start.toEpochMilliseconds(),
                end = period.end.toEpochMilliseconds()
            )
            .mapLatest { incomes -> incomes.map { it.asExternalModel() } }

    override suspend fun getIncomesByCategory(
        userId: String,
        categoryId: String,
        period: Period
    ): Flow<List<Income>> =
        incomeDao
            .getIncomesByCategory(
                userId = userId,
                categoryId = categoryId,
                start = period.start.toEpochMilliseconds(),
                end = period.end.toEpochMilliseconds()
            )
            .mapLatest { incomes -> incomes.map { it.asExternalModel() } }

    override suspend fun addIncome(income: Income) {
        try {
            Timber.d("Adding income = ${income.name}")

            // Ensure the insert is non-cancellable if that's the desired behavior
            withContext(NonCancellable) {
                incomeDao.insertIncome(income.asEntity())
            }

            Timber.d("income added successfully = ${income.name}")
        } catch (e: CancellationException) {
            // Handle coroutine cancellation specifically
            Timber.e("Coroutine was cancelled while adding income = ${income.name}")
            throw e // Re-throw cancellation exceptions
        } catch (e: Exception) {
            Timber.e(e, "Error adding income = ${income.name}")
        }
    }

    override suspend fun updateIncome(income: Income) {
        incomeDao.updateIncome(income.asEntity())
    }

    override suspend fun deleteIncome(userId: String, incomeId: String) {
        incomeDao.deleteIncome(userId, incomeId)
    }

    override suspend fun getNonSyncedIncomes(userId: String): List<Income> =
        incomeDao.getNonSyncedIncomes(userId).map { it.asExternalModel() }
}