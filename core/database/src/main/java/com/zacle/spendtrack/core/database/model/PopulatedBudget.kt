package com.zacle.spendtrack.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.zacle.spendtrack.core.model.Budget

data class PopulatedBudget(
    @Embedded val budget: BudgetEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: CategoryEntity
)

fun PopulatedBudget.asExternalModel() = Budget(
    budgetId = budget.id,
    userId = budget.userId,
    category = category.asExternalModel(),
    remainingAmount = budget.remainingAmount,
    budgetAlert = budget.budgetAlert,
    budgetAlertPercentage = budget.budgetAlertPercentage,
    budgetPeriod = budget.budgetPeriod,
    createdAt = budget.createdAt,
    updatedAt = budget.updatedAt,
    amount = budget.amount,
    recurrent = budget.recurrent,
    synced = budget.synced
)
