package com.zacle.spendtrack.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.zacle.spendtrack.core.model.Expense

data class PopulatedExpense(
    @Embedded val expense: ExpenseEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: CategoryEntity
)

fun PopulatedExpense.asExternalModel() = Expense(
    expenseId = expense.expenseId,
    userId = expense.userId,
    category = category.asExternalModel(),
    transactionDate = expense.transactionDate,
    receiptUrl = expense.receiptUrl,
    updatedAt = expense.updatedAt,
    name = expense.name,
    description = expense.description,
    amount = expense.amount,
    synced = expense.synced
)
