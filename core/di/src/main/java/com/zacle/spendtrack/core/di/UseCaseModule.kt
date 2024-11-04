package com.zacle.spendtrack.core.di

import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.domain.GetReportUseCase
import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.GetUserDataAndAuthStateUseCase
import com.zacle.spendtrack.core.domain.HomeUseCase
import com.zacle.spendtrack.core.domain.OverviewUseCase
import com.zacle.spendtrack.core.domain.TransactionsUseCase
import com.zacle.spendtrack.core.domain.auth.ObserveUserAuthStateUseCase
import com.zacle.spendtrack.core.domain.budget.GetBudgetsUseCase
import com.zacle.spendtrack.core.domain.category.GetCategoriesUseCase
import com.zacle.spendtrack.core.domain.datastore.GetUserDataUseCase
import com.zacle.spendtrack.core.domain.expense.GetExpensesUseCase
import com.zacle.spendtrack.core.domain.income.GetIncomesUseCase
import com.zacle.spendtrack.core.domain.repository.CategoryRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideUseCaseConfiguration(
        @STDispatcher(IO) ioDispatcher: CoroutineDispatcher
    ): UseCase.Configuration = UseCase.Configuration(ioDispatcher)

    @Provides
    fun provideUserDataAndAuthStateUseCase(
        configuration: UseCase.Configuration,
        getUserDataUseCase: GetUserDataUseCase,
        observeUserAuthStateUseCase: ObserveUserAuthStateUseCase
    ): GetUserDataAndAuthStateUseCase =
        GetUserDataAndAuthStateUseCase(configuration, getUserDataUseCase, observeUserAuthStateUseCase)

    @Provides
    fun provideGetCategoriesUseCase(
        configuration: UseCase.Configuration,
        categoryRepository: CategoryRepository
    ): GetCategoriesUseCase = GetCategoriesUseCase(configuration, categoryRepository)

    @Provides
    fun provideOverviewUseCase(
        configuration: UseCase.Configuration,
        getExpensesUseCase: GetExpensesUseCase,
        getIncomesUseCase: GetIncomesUseCase
    ): OverviewUseCase = OverviewUseCase(configuration, getExpensesUseCase, getIncomesUseCase)

    @Provides
    fun provideTransactionsUseCase(
        configuration: UseCase.Configuration,
        getExpensesUseCase: GetExpensesUseCase,
        getIncomesUseCase: GetIncomesUseCase
    ): TransactionsUseCase = TransactionsUseCase(configuration, getExpensesUseCase, getIncomesUseCase)

    @Provides
    fun provideHomeUseCase(
        configuration: UseCase.Configuration,
        overviewUseCase: OverviewUseCase,
        transactionsUseCase: TransactionsUseCase,
        budgetsUseCase: GetBudgetsUseCase
    ): HomeUseCase = HomeUseCase(configuration, overviewUseCase, transactionsUseCase, budgetsUseCase)

    @Provides
    fun provideGetReportUseCase(
        configuration: UseCase.Configuration,
        expenseRepository: ExpenseRepository,
        incomeRepository: IncomeRepository
    ): GetReportUseCase = GetReportUseCase(configuration, expenseRepository, incomeRepository)
}