package com.zacle.spendtrack.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.zacle.spendtrack.core.database.model.BudgetEntity
import com.zacle.spendtrack.core.database.model.PopulatedBudget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Transaction
    @Query("SELECT * FROM budgets WHERE user_id = :userId AND budget_period BETWEEN :start AND :end")
    fun getBudgets(userId: String, start: Long, end: Long): Flow<List<PopulatedBudget>>

    @Transaction
    @Query(
        "SELECT * " +
        "FROM budgets " +
        "WHERE user_id = :userId AND id = :budgetId AND budget_period BETWEEN :start AND :end")
    fun getBudget(userId: String, budgetId: String, start: Long, end: Long): Flow<PopulatedBudget?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)
}