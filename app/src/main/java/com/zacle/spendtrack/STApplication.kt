package com.zacle.spendtrack

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.data.sync.SyncBudgetWorker
import com.zacle.spendtrack.core.data.sync.SyncExpenseWorker
import com.zacle.spendtrack.core.data.sync.SyncIncomeWorker
import com.zacle.spendtrack.core.datastore.UserPreferencesDataSource
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class STApplication: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: STWorkersFactory

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
}


class STWorkersFactory @Inject constructor(
    budgetRepository: BudgetRepository,
    expenseRepository: ExpenseRepository,
    incomeRepository: IncomeRepository,
    userPreferencesDataSource: UserPreferencesDataSource,
    @STDispatcher(IO) ioDispatcher: CoroutineDispatcher
): DelegatingWorkerFactory() {
    init {
        addFactory(
            SyncBudgetWorker.Factory(
                budgetRepository = budgetRepository,
                userPreferencesDataSource = userPreferencesDataSource,
                ioDispatcher = ioDispatcher
            )
        )
        addFactory(
            SyncExpenseWorker.Factory(
                expenseRepository = expenseRepository,
                userPreferencesDataSource = userPreferencesDataSource,
                ioDispatcher = ioDispatcher
            )
        )
        addFactory(
            SyncIncomeWorker.Factory(
                incomeRepository = incomeRepository,
                userPreferencesDataSource = userPreferencesDataSource,
                ioDispatcher = ioDispatcher
            )
        )
    }
}