package com.zacle.spendtrack.core.di

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.budget.AddBudgetUseCase
import com.zacle.spendtrack.core.domain.budget.DeleteBudgetUseCase
import com.zacle.spendtrack.core.domain.budget.GetBudgetDetailsUseCase
import com.zacle.spendtrack.core.domain.budget.GetBudgetUseCase
import com.zacle.spendtrack.core.domain.budget.GetBudgetsUseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BudgetUseCaseModule {
    @Provides
    @Singleton
    fun provideAddBudgetUseCase(
        configuration: UseCase.Configuration,
        budgetRepository: BudgetRepository
    ): AddBudgetUseCase = AddBudgetUseCase(configuration, budgetRepository)

    @Provides
    @Singleton
    fun provideGetBudgetsUseCase(
        configuration: UseCase.Configuration,
        budgetRepository: BudgetRepository
    ): GetBudgetsUseCase = GetBudgetsUseCase(configuration, budgetRepository)

    @Provides
    @Singleton
    fun provideDeleteBudgetUseCase(
        configuration: UseCase.Configuration,
        budgetRepository: BudgetRepository
    ): DeleteBudgetUseCase = DeleteBudgetUseCase(configuration, budgetRepository)

    @Provides
    @Singleton
    fun provideGetBudgetUseCase(
        configuration: UseCase.Configuration,
        budgetRepository: BudgetRepository
    ): GetBudgetUseCase = GetBudgetUseCase(configuration, budgetRepository)

    @Provides
    @Singleton
    fun provideGetBudgetDetailsUseCase(
        configuration: UseCase.Configuration,
        budgetRepository: BudgetRepository,
        expenseRepository: ExpenseRepository,
        incomeRepository: IncomeRepository
    ): GetBudgetDetailsUseCase =
        GetBudgetDetailsUseCase(configuration, budgetRepository, expenseRepository, incomeRepository)


}