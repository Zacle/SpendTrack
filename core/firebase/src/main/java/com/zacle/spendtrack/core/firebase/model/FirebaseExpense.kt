package com.zacle.spendtrack.core.firebase.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.Expense
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant

data class FirebaseExpense(
    @DocumentId val expenseId: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val receiptUrl: String? = null,
    val updatedAt: Timestamp? = null,
    val amount: Double = 0.0,
    val category: Category = Category(),
    val transactionDate: Timestamp = Timestamp.now()
)

fun FirebaseExpense.asExternalModel() = Expense(
    id = expenseId,
    userId = userId,
    name = name,
    description = description,
    receiptUrl = receiptUrl,
    updatedAt = updatedAt?.let { Instant.fromEpochSeconds(it.seconds, it.nanoseconds) },
    amount = amount,
    category = category,
    transactionDate = Instant.fromEpochSeconds(transactionDate.seconds, transactionDate.nanoseconds),
    synced = true
)

fun Expense.asFirebaseModel() = FirebaseExpense(
    expenseId = id,
    userId = userId,
    name = name,
    description = description,
    receiptUrl = receiptUrl,
    updatedAt = updatedAt?.toJavaInstant()?.let { Timestamp(it.epochSecond, it.nano) },
    amount = amount,
    category = category,
    transactionDate = Timestamp(transactionDate.epochSeconds, transactionDate.nanosecondsOfSecond)
)
