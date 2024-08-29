package com.zacle.spendtrack.core.firebase.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Category
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant

data class FirebaseBudget(
    @DocumentId val budgetId: String = "",
    val userId: String = "",
    val category: Category = Category(),
    val amount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val budgetAlert: Boolean = false,
    val budgetAlertPercentage: Int = 0,
    val budgetPeriod: Timestamp = Timestamp.now(),
    val recurrent: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null
)

fun FirebaseBudget.asExternalModel() = Budget(
    budgetId = budgetId,
    userId = userId,
    category = category,
    amount = amount,
    remainingAmount = remainingAmount,
    budgetAlert = budgetAlert,
    budgetAlertPercentage = budgetAlertPercentage,
    budgetPeriod = Instant.fromEpochSeconds(budgetPeriod.seconds, budgetPeriod.nanoseconds),
    recurrent = recurrent,
    createdAt = Instant.fromEpochSeconds(createdAt.seconds, createdAt.nanoseconds),
    updatedAt = updatedAt?.let { Instant.fromEpochSeconds(it.seconds, it.nanoseconds) },
    synced = true
)

fun Budget.asFirebaseModel() = FirebaseBudget(
    budgetId = budgetId,
    userId = userId,
    category = category,
    amount = amount,
    remainingAmount = remainingAmount,
    budgetAlert = budgetAlert,
    budgetAlertPercentage = budgetAlertPercentage,
    budgetPeriod = Timestamp(budgetPeriod.epochSeconds, budgetPeriod.nanosecondsOfSecond),
    recurrent = recurrent,
    createdAt = Timestamp(createdAt.epochSeconds, createdAt.nanosecondsOfSecond),
    updatedAt = updatedAt?.toJavaInstant()?.let { Timestamp(it.epochSecond, it.nano) }
)