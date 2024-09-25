package com.zacle.spendtrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.zacle.spendtrack.core.database.model.IncomeEntity
import com.zacle.spendtrack.core.database.model.PopulatedIncome
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Transaction
    @Query("SELECT * FROM incomes WHERE user_id = :userId AND transaction_date BETWEEN :start AND :end")
    fun getIncomes(userId: String, start: Long, end: Long): Flow<List<PopulatedIncome>>

    @Transaction
    @Query("SELECT * FROM incomes WHERE user_id = :userId AND income_id = :incomeId")
    fun getIncome(userId: String, incomeId: String): Flow<PopulatedIncome?>

    @Transaction
    @Query("SELECT * FROM incomes WHERE user_id = :userId AND category_id = :categoryId AND transaction_date BETWEEN :start AND :end")
    fun getIncomesByCategory(userId: String, categoryId: String, start: Long, end: Long): Flow<List<PopulatedIncome>>

    @Transaction
    @Query("SELECT * FROM incomes WHERE user_id = :userId AND synced = 0")
    suspend fun getNonSyncedIncomes(userId: String): List<PopulatedIncome>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncome(income: IncomeEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIncomes(incomes: List<IncomeEntity>)

    @Update
    suspend fun updateIncome(income: IncomeEntity)

    @Query("DELETE FROM incomes WHERE user_id = :userId AND income_id = :incomeId")
    suspend fun deleteIncome(userId: String, incomeId: String)

}