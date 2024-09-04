package com.zacle.spendtrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zacle.spendtrack.core.database.model.DeletedExpenseEntity

@Dao
interface DeletedExpenseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deletedExpense: DeletedExpenseEntity)

    @Query("DELETE FROM deleted_expenses WHERE user_id = :userId AND expense_id = :expenseId")
    suspend fun delete(userId: String, expenseId: String)


    @Query("SELECT * FROM deleted_expenses WHERE user_id = :userId")
    suspend fun getDeletedExpenses(userId: String): List<DeletedExpenseEntity>
}