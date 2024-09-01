package com.zacle.spendtrack.core.di

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.expense.AddExpenseUseCase
import com.zacle.spendtrack.core.domain.expense.DeleteExpenseUseCase
import com.zacle.spendtrack.core.domain.expense.GetExpenseUseCase
import com.zacle.spendtrack.core.domain.expense.GetExpensesUseCase
import com.zacle.spendtrack.core.domain.expense.UpdateExpenseUseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExpenseUseCaseModule {
    @Provides
    @Singleton
    fun provideAddExpenseUseCase(
        configuration: UseCase.Configuration,
        expenseRepository: ExpenseRepository,
        budgetRepository: BudgetRepository
    ): AddExpenseUseCase = AddExpenseUseCase(configuration, expenseRepository, budgetRepository)

    @Provides
    @Singleton
    fun provideDeleteExpenseUseCase(
        configuration: UseCase.Configuration,
        expenseRepository: ExpenseRepository,
        budgetRepository: BudgetRepository
    ): DeleteExpenseUseCase = DeleteExpenseUseCase(configuration, expenseRepository, budgetRepository)

    @Provides
    @Singleton
    fun provideUpdateExpenseUseCase(
        configuration: UseCase.Configuration,
        expenseRepository: ExpenseRepository,
        budgetRepository: BudgetRepository
    ): UpdateExpenseUseCase = UpdateExpenseUseCase(configuration, expenseRepository, budgetRepository)

    @Provides
    @Singleton
    fun provideGetExpensesUseCase(
        configuration: UseCase.Configuration,
        expenseRepository: ExpenseRepository
    ): GetExpensesUseCase = GetExpensesUseCase(configuration, expenseRepository)

    @Provides
    @Singleton
    fun providesGetExpenseUseCase(
        configuration: UseCase.Configuration,
        expenseRepository: ExpenseRepository
    ): GetExpenseUseCase = GetExpenseUseCase(configuration, expenseRepository)
}