package com.zacle.spendtrack.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.common.di.ApplicationScope
import com.zacle.spendtrack.core.database.STDatabase
import com.zacle.spendtrack.core.database.dao.BudgetDao
import com.zacle.spendtrack.core.database.dao.CategoryDao
import com.zacle.spendtrack.core.database.dao.DeletedBudgetDao
import com.zacle.spendtrack.core.database.dao.DeletedExpenseDao
import com.zacle.spendtrack.core.database.dao.DeletedIncomeDao
import com.zacle.spendtrack.core.database.dao.ExpenseDao
import com.zacle.spendtrack.core.database.dao.IncomeDao
import com.zacle.spendtrack.core.database.dao.UserDao
import com.zacle.spendtrack.core.database.ioThread
import com.zacle.spendtrack.core.database.model.CATEGORIES
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun provideSTDatabase(
        @ApplicationContext context: Context,
        @STDispatcher(IO) dispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        categoryDaoProvider: Provider<CategoryDao>
    ): STDatabase = Room.databaseBuilder(
        context,
        STDatabase::class.java,
        "spendtrack-database"
    )
        .addCallback(
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    ioThread {
                        scope.launch(dispatcher) {
                            categoryDaoProvider.get().insertAll(CATEGORIES)
                        }
                    }
                }
            }
        )
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