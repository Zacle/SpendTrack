package com.zacle.spendtrack.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zacle.spendtrack.core.model.Budget
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id", "category_id"])]
)
data class BudgetEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    @ColumnInfo(name = "remaining_amount")
    val remainingAmount: Double = 0.0,
    @ColumnInfo(name = "budget_alert")
    val budgetAlert: Boolean = false,
    @ColumnInfo(name = "budget_alert_percentage")
    val budgetAlertPercentage: Int = 80,
    @ColumnInfo(name = "budget_period")
    val budgetPeriod: Instant,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Clock.System.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant? = null,
    val amount: Double = 0.0,
    val recurrent: Boolean = false,
    val synced: Boolean = false
)

fun Budget.asEntity() = BudgetEntity(
    id = budgetId,
    userId = userId,
    categoryId = category.categoryId,
    remainingAmount = remainingAmount,
    budgetAlert = budgetAlert,
    budgetAlertPercentage = budgetAlertPercentage,
    budgetPeriod = budgetPeriod,
    createdAt = createdAt,
    updatedAt = updatedAt,
    amount = amount,
    recurrent = recurrent,
    synced = synced
)
