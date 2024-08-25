package com.zacle.spendtrack.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.zacle.spendtrack.core.model.Income

data class PopulatedIncome(
    @Embedded val income: IncomeEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: CategoryEntity
)

fun PopulatedIncome.asExternalModel() = Income(
    incomeId = income.incomeId,
    userId = income.userId,
    category = category.asExternalModel(),
    transactionDate = income.transactionDate,
    receiptUrl = income.receiptUrl,
    updatedAt = income.updatedAt,
    name = income.name,
    description = income.description,
    amount = income.amount,
    synced = income.synced
)
