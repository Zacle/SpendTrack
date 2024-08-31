package com.zacle.spendtrack.core.database.di

import com.zacle.spendtrack.core.common.di.LocalBudgetData
import com.zacle.spendtrack.core.common.di.LocalExpenseData
import com.zacle.spendtrack.core.common.di.LocalIncomeData
import com.zacle.spendtrack.core.common.di.LocalUserData
import com.zacle.spendtrack.core.data.datasource.BudgetDataSource
import com.zacle.spendtrack.core.data.datasource.CategoryDataSource
import com.zacle.spendtrack.core.data.datasource.ExpenseDataSource
import com.zacle.spendtrack.core.data.datasource.IncomeDataSource
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.database.dao.BudgetDao
import com.zacle.spendtrack.core.database.dao.CategoryDao
import com.zacle.spendtrack.core.database.dao.ExpenseDao
import com.zacle.spendtrack.core.database.dao.IncomeDao
import com.zacle.spendtrack.core.database.dao.UserDao
import com.zacle.spendtrack.core.database.datasource.LocalBudgetDataSource
import com.zacle.spendtrack.core.database.datasource.LocalCategoryDataSource
import com.zacle.spendtrack.core.database.datasource.LocalExpenseDataSource
import com.zacle.spendtrack.core.database.datasource.LocalIncomeDataSource
import com.zacle.spendtrack.core.database.datasource.LocalUserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataSourceModule {
    @Provides
    @Singleton
    @LocalUserData
    fun provideLocalUserDataSource(userDao: UserDao): UserDataSource = LocalUserDataSource(userDao)

    @Provides
    @Singleton
    @LocalBudgetData
    fun provideLocalBudgetDataSource(budgetDao: BudgetDao): BudgetDataSource =
        LocalBudgetDataSource(budgetDao)

    @Provides
    @Singleton
    @LocalExpenseData
    fun provideLocalExpenseDataSource(expenseDao: ExpenseDao): ExpenseDataSource =
        LocalExpenseDataSource(expenseDao)

    @Provides
    @Singleton
    @LocalIncomeData
    fun provideLocalIncomeDataSource(incomeDao: IncomeDao): IncomeDataSource =
        LocalIncomeDataSource(incomeDao)

    @Provides
    @Singleton
    fun provideLocalCategoryDataSource(categoryDao: CategoryDao): CategoryDataSource =
        LocalCategoryDataSource(categoryDao)
}