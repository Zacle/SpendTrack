package com.zacle.spendtrack.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zacle.spendtrack.core.model.DeletedIncome

@Entity(tableName = "deleted_incomes")
data class DeletedIncomeEntity(
    @PrimaryKey @ColumnInfo(name = "income_id") val incomeId: String,
    @ColumnInfo(name = "user_id") val userId: String
)

fun DeletedIncome.asEntity() = DeletedIncomeEntity(incomeId, userId)

fun DeletedIncomeEntity.asExternalModel() = DeletedIncome(incomeId, userId)
