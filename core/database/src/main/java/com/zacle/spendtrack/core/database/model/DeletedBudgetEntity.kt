package com.zacle.spendtrack.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zacle.spendtrack.core.model.DeletedBudget

@Entity(tableName = "deleted_budgets")
data class DeletedBudgetEntity(
    @PrimaryKey @ColumnInfo(name = "budget_id") val budgetId: String,
    @ColumnInfo(name = "user_id") val userId: String
)

fun DeletedBudgetEntity.asExternalModel() = DeletedBudget(
    budgetId = budgetId,
    userId = userId
)

fun DeletedBudget.asEntity() = DeletedBudgetEntity(
    budgetId = budgetId,
    userId = userId
)
