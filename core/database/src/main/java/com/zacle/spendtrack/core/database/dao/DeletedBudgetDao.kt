package com.zacle.spendtrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zacle.spendtrack.core.database.model.DeletedBudgetEntity

@Dao
interface DeletedBudgetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deletedBudget: DeletedBudgetEntity)

    @Query("DELETE FROM deleted_budgets WHERE user_id = :userId AND budget_id = :budgetId")
    suspend fun delete(userId: String, budgetId: String)

    @Query("SELECT * FROM deleted_budgets WHERE user_id = :userId")
    suspend fun getDeletedBudgets(userId: String): List<DeletedBudgetEntity>

}