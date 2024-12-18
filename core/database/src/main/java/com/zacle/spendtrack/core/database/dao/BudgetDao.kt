package com.zacle.spendtrack.core.database.dao

import androidx.room.Dao
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
        "WHERE user_id = :userId AND id = :budgetId")
    fun getBudget(userId: String, budgetId: String): Flow<PopulatedBudget?>

    @Transaction
    @Query("SELECT * FROM budgets WHERE user_id = :userId AND synced = 0")
    suspend fun getNonSyncedBudgets(userId: String): List<PopulatedBudget>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllBudgets(budgets: List<BudgetEntity>)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE user_id = :userId AND id = :budgetId")
    suspend fun deleteBudget(userId: String, budgetId: String)
}