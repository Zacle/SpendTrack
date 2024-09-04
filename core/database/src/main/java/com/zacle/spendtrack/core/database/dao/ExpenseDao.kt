package com.zacle.spendtrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.zacle.spendtrack.core.database.model.ExpenseEntity
import com.zacle.spendtrack.core.database.model.PopulatedExpense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Transaction
    @Query(
        "SELECT * " +
        "FROM expenses " +
        "WHERE user_id = :userId AND expense_id = :expenseId"
    )
    fun getExpense(userId: String, expenseId: String): Flow<PopulatedExpense?>

    @Transaction
    @Query("SELECT * FROM expenses WHERE user_id = :userId AND transaction_date BETWEEN :start AND :end")
    fun getExpenses(userId: String, start: Long, end: Long): Flow<List<PopulatedExpense>>

    @Transaction
    @Query(
        "SELECT * " +
        "FROM expenses " +
        "WHERE user_id = :userId AND category_id = :categoryId AND transaction_date BETWEEN :start AND :end"
    )
    fun getExpensesByCategory(userId: String, categoryId: String, start: Long, end: Long): Flow<List<PopulatedExpense>>

    @Transaction
    @Query("SELECT * FROM expenses WHERE user_id = :userId AND synced = 0")
    suspend fun getNonSyncedExpenses(userId: String): List<PopulatedExpense>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE user_id = :userId AND expense_id = :expenseId")
    suspend fun deleteExpense(userId: String, expenseId: String)

}