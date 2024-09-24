package com.zacle.spendtrack.core.database.di

import android.content.Context
import androidx.room.Room
import com.zacle.spendtrack.core.database.DatabaseMigrations.MIGRATION_2_3
import com.zacle.spendtrack.core.database.DatabaseMigrations.MIGRATION_3_4
import com.zacle.spendtrack.core.database.STDatabase
import com.zacle.spendtrack.core.database.dao.BudgetDao
import com.zacle.spendtrack.core.database.dao.CategoryDao
import com.zacle.spendtrack.core.database.dao.DeletedBudgetDao
import com.zacle.spendtrack.core.database.dao.DeletedExpenseDao
import com.zacle.spendtrack.core.database.dao.DeletedIncomeDao
import com.zacle.spendtrack.core.database.dao.ExpenseDao
import com.zacle.spendtrack.core.database.dao.IncomeDao
import com.zacle.spendtrack.core.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun provideSTDatabase(
        @ApplicationContext context: Context
    ): STDatabase = Room.databaseBuilder(
        context,
        STDatabase::class.java,
        "spendtrack-database"
    )
        .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
        .build()

    @Provides
    fun provideUserDao(
        database: STDatabase,
    ): UserDao = database.userDao()

    @Provides
    fun provideCategoryDao(
        database: STDatabase,
    ): CategoryDao = database.categoryDao()

    @Provides
    fun provideBudgetDao(
        database: STDatabase,
    ): BudgetDao = database.budgetDao()

    @Provides
    fun provideExpenseDao(
        database: STDatabase,
    ): ExpenseDao = database.expenseDao()

    @Provides
    fun provideIncomeDao(
        database: STDatabase,
    ): IncomeDao = database.incomeDao()

    @Provides
    fun provideDeletedBudgetDao(
        database: STDatabase,
    ): DeletedBudgetDao = database.deletedBudgetDao()

    @Provides
    fun provideDeletedExpenseDao(
        database: STDatabase,
    ): DeletedExpenseDao = database.deletedExpenseDao()

    @Provides
    fun provideDeletedIncomeDao(
        database: STDatabase,
    ): DeletedIncomeDao = database.deletedIncomeDao()
}