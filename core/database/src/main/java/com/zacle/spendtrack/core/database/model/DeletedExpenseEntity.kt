package com.zacle.spendtrack.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zacle.spendtrack.core.model.DeletedExpense

@Entity(tableName = "deleted_expenses")
data class DeletedExpenseEntity(
    @PrimaryKey @ColumnInfo(name = "expense_id") val expenseId: String,
    @ColumnInfo(name = "user_id") val userId: String
)

fun DeletedExpenseEntity.asExternalModel() = DeletedExpense(
    expenseId = expenseId,
    userId = userId
)

fun DeletedExpense.asEntity() = DeletedExpenseEntity(
    expenseId = expenseId,
    userId = userId
)
