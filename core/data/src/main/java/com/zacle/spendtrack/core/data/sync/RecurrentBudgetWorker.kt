package com.zacle.spendtrack.core.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.zacle.spendtrack.core.data.Constants.BUDGET_ID_KEY
import com.zacle.spendtrack.core.data.Constants.USER_ID_KEY
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.model.Budget
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * A [CoroutineWorker] to create a recurrent budget at the start of every month.
 */
@HiltWorker
class RecurrentBudgetWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val budgetRepository: BudgetRepository
): CoroutineWorker(context, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        context.syncBudgetForegroundInfo()

    override suspend fun doWork(): Result =
        try {
            val userId = inputData.getString(USER_ID_KEY)
                ?: throw IllegalArgumentException("Missing userId argument")
            val budgetId = inputData.getString(BUDGET_ID_KEY)
                ?: throw IllegalArgumentException("Missing budgetId argument")

            // Fetch the recurrent budget
            val budget = budgetRepository.getBudget(userId, budgetId).first()

            if (budget != null && budget.recurrent) {
                val nextBudget = Budget(
                    userId = budget.userId,
                    category = budget.category,
                    amount = budget.amount,
                    remainingAmount = 0.0,
                    budgetAlert = budget.budgetAlert,
                    budgetAlertPercentage = budget.budgetAlertPercentage,
                    budgetPeriod = budget.budgetPeriod,
                    recurrent = true
                )
                // Set the budget for the next period in the database
                budgetRepository.addBudget(nextBudget)

                // Reschedule for the next month after successfully completing the work
                scheduleNextRecurrentBudgetWork(nextBudget, context)

                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure()
        }

    companion object {
        /**
         * Schedules a work to be run at the start of the next month.
         *
         * @param budget The budget to be set for the next month.
         * @param context The context used to request the work.
         */
        fun scheduleNextRecurrentBudgetWork(budget: Budget, context: Context) {
            val inputData = Data.Builder()
                .putString(USER_ID_KEY, budget.userId)
                .putString(BUDGET_ID_KEY, budget.budgetId)
                .build()

            val delayToNextMonth = calculateDelayToNextMonth()

            val workRequest = OneTimeWorkRequestBuilder<RecurrentBudgetWorker>()
                .setInitialDelay(delayToNextMonth, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "RecurrentBudgetWork_${budget.category.categoryId}",
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        }

        private fun calculateDelayToNextMonth(): Long {
            val currentDate = LocalDateTime.now()

            // Set the target time to the first day of the next month at midnight
            val nextMonth = currentDate.plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(30)

            // Calculate the delay in milliseconds
            return Duration.between(currentDate, nextMonth).toMillis()
        }
    }

    class Factory(
        private val budgetRepository: BudgetRepository
    ): WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? =
            if (workerClassName == RecurrentBudgetWorker::class.java.name) {
                RecurrentBudgetWorker(
                    context = appContext,
                    workerParams = workerParameters,
                    budgetRepository = budgetRepository
                )
            } else {
                null
            }
    }
}