package com.zacle.spendtrack.core.database.di

import com.zacle.spendtrack.core.common.di.LocalBudgetData
import com.zacle.spendtrack.core.common.di.LocalExpenseData
import com.zacle.spendtrack.core.common.di.LocalIncomeData
import com.zacle.spendtrack.core.common.di.LocalUserData
import com.zacle.spendtrack.core.data.datasource.CategoryDataSource
import com.zacle.spendtrack.core.data.datasource.DeletedBudgetDataSource
import com.zacle.spendtrack.core.data.datasource.DeletedExpenseDataSource
import com.zacle.spendtrack.core.data.datasource.DeletedIncomeDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableBudgetDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableExpenseDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableIncomeDataSource
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.database.dao.BudgetDao
import com.zacle.spendtrack.core.database.dao.CategoryDao
import com.zacle.spendtrack.core.database.dao.DeletedBudgetDao
import com.zacle.spendtrack.core.database.dao.DeletedExpenseDao
import com.zacle.spendtrack.core.database.dao.DeletedIncomeDao
import com.zacle.spendtrack.core.database.dao.ExpenseDao
import com.zacle.spendtrack.core.database.dao.IncomeDao
import com.zacle.spendtrack.core.database.dao.UserDao
import com.zacle.spendtrack.core.database.datasource.LocalBudgetDataSource
import com.zacle.spendtrack.core.database.datasource.LocalCategoryDataSource
import com.zacle.spendtrack.core.database.datasource.LocalDeletedBudgetDataSource
import com.zacle.spendtrack.core.database.datasource.LocalDeletedExpenseDataSource
import com.zacle.spendtrack.core.database.datasource.LocalDeletedIncomeDataSource
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
    fun provideLocalBudgetDataSource(budgetDao: BudgetDao): SyncableBudgetDataSource =
        LocalBudgetDataSource(budgetDao)

    @Provides
    @Singleton
    @LocalExpenseData
    fun provideLocalExpenseDataSource(expenseDao: ExpenseDao): SyncableExpenseDataSource =
        LocalExpenseDataSource(expenseDao)

    @Provides
    @Singleton
    @LocalIncomeData
    fun provideLocalIncomeDataSource(incomeDao: IncomeDao): SyncableIncomeDataSource =
        LocalIncomeDataSource(incomeDao)

    @Provides
    @Singleton
    fun provideLocalCategoryDataSource(categoryDao: CategoryDao): CategoryDataSource =
        LocalCategoryDataSource(categoryDao)

    @Provides
    @Singleton
    fun provideLocalDeletedBudgetDataSource(deletedBudgetDao: DeletedBudgetDao): DeletedBudgetDataSource =
        LocalDeletedBudgetDataSource(deletedBudgetDao)

    @Provides
    @Singleton
    fun provideLocalDeletedExpenseDataSource(deletedExpenseDao: DeletedExpenseDao): DeletedExpenseDataSource =
        LocalDeletedExpenseDataSource(deletedExpenseDao)

    @Provides
    @Singleton
    fun provideLocalDeletedIncomeDataSource(deletedIncomeDao: DeletedIncomeDao): DeletedIncomeDataSource =
        LocalDeletedIncomeDataSource(deletedIncomeDao)
}