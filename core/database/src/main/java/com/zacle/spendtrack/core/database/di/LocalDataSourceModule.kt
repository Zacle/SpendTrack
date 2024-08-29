package com.zacle.spendtrack.core.database.di

import com.zacle.spendtrack.core.common.di.LocalBudgetData
import com.zacle.spendtrack.core.common.di.LocalUserData
import com.zacle.spendtrack.core.data.datasource.BudgetDataSource
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.database.dao.BudgetDao
import com.zacle.spendtrack.core.database.dao.UserDao
import com.zacle.spendtrack.core.database.datasource.LocalBudgetDataSource
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

}