package com.zacle.spendtrack.core.di

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.income.AddIncomeUseCase
import com.zacle.spendtrack.core.domain.income.DeleteIncomeUseCase
import com.zacle.spendtrack.core.domain.income.GetIncomeUseCase
import com.zacle.spendtrack.core.domain.income.GetIncomesUseCase
import com.zacle.spendtrack.core.domain.income.UpdateIncomeUseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IncomeUseCaseModule {
    @Provides
    @Singleton
    fun provideGetIncomesUseCase(
        configuration: UseCase.Configuration,
        incomeRepository: IncomeRepository
    ): GetIncomesUseCase = GetIncomesUseCase(configuration, incomeRepository)

    @Provides
    @Singleton
    fun provideGetIncomeUseCase(
        configuration: UseCase.Configuration,
        incomeRepository: IncomeRepository
    ): GetIncomeUseCase = GetIncomeUseCase(configuration, incomeRepository)

    @Provides
    @Singleton
    fun provideAddIncomeUseCase(
        configuration: UseCase.Configuration,
        incomeRepository: IncomeRepository,
        budgetRepository: BudgetRepository
    ): AddIncomeUseCase = AddIncomeUseCase(configuration, incomeRepository, budgetRepository)

    @Provides
    @Singleton
    fun provideDeleteIncomeUseCase(
        configuration: UseCase.Configuration,
        incomeRepository: IncomeRepository,
        budgetRepository: BudgetRepository
    ): DeleteIncomeUseCase = DeleteIncomeUseCase(configuration, incomeRepository, budgetRepository)

    @Provides
    @Singleton
    fun providesUpdateIncomeUseCase(
        configuration: UseCase.Configuration,
        incomeRepository: IncomeRepository,
        budgetRepository: BudgetRepository
    ): UpdateIncomeUseCase = UpdateIncomeUseCase(configuration, incomeRepository, budgetRepository)
}