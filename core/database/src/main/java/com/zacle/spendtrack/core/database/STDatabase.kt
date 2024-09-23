package com.zacle.spendtrack.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zacle.spendtrack.core.database.converters.InstantConverter
import com.zacle.spendtrack.core.database.dao.BudgetDao
import com.zacle.spendtrack.core.database.dao.CategoryDao
import com.zacle.spendtrack.core.database.dao.DeletedBudgetDao
import com.zacle.spendtrack.core.database.dao.DeletedExpenseDao
import com.zacle.spendtrack.core.database.dao.DeletedIncomeDao
import com.zacle.spendtrack.core.database.dao.ExpenseDao
import com.zacle.spendtrack.core.database.dao.IncomeDao
import com.zacle.spendtrack.core.database.dao.UserDao
import com.zacle.spendtrack.core.database.model.BudgetEntity
import com.zacle.spendtrack.core.database.model.CategoryEntity
import com.zacle.spendtrack.core.database.model.DeletedBudgetEntity
import com.zacle.spendtrack.core.database.model.DeletedExpenseEntity
import com.zacle.spendtrack.core.database.model.DeletedIncomeEntity
import com.zacle.spendtrack.core.database.model.ExpenseEntity
import com.zacle.spendtrack.core.database.model.IncomeEntity
import com.zacle.spendtrack.core.database.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        ExpenseEntity::class,
        IncomeEntity::class,
        DeletedBudgetEntity::class,
        DeletedExpenseEntity::class,
        DeletedIncomeEntity::class
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(
    InstantConverter::class
)
abstract class STDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun deletedBudgetDao(): DeletedBudgetDao
    abstract fun deletedExpenseDao(): DeletedExpenseDao
    abstract fun deletedIncomeDao(): DeletedIncomeDao
}