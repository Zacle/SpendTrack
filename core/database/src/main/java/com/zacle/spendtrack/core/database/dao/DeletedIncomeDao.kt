package com.zacle.spendtrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zacle.spendtrack.core.database.model.DeletedIncomeEntity

@Dao
interface DeletedIncomeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deletedIncome: DeletedIncomeEntity)

    @Query("DELETE FROM deleted_incomes WHERE user_id = :userId AND income_id = :incomeId")
    suspend fun delete(userId: String, incomeId: String)

    @Query("SELECT * FROM deleted_incomes WHERE user_id = :userId")
    suspend fun getDeletedIncomes(userId: String): List<DeletedIncomeEntity>
}