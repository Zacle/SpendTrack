package com.zacle.spendtrack.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zacle.spendtrack.core.model.Expense
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("category_id")]
)
data class ExpenseEntity(
    @PrimaryKey
    @ColumnInfo(name = "expense_id")
    val expenseId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    @ColumnInfo(name = "transaction_date")
    val transactionDate: Instant = Clock.System.now(),
    @ColumnInfo(name = "receipt_url")
    val receiptUrl: String? = null,
    @ColumnInfo(name = "local_receipt_image_path")
    val localReceiptImagePath: String? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant? = null,
    val name: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val synced: Boolean = false
)

fun Expense.asEntity() = ExpenseEntity(
    expenseId = id,
    userId = userId,
    categoryId = category.categoryId,
    transactionDate = transactionDate,
    receiptUrl = receiptUrl,
    localReceiptImagePath = localReceiptImagePath,
    updatedAt = updatedAt,
    name = name,
    description = description,
    amount = amount,
    synced = synced
)
