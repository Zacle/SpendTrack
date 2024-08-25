package com.zacle.spendtrack.core.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

data class Budget(
    val budgetId: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val category: Category = Category(),
    val amount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val budgetAlert: Boolean = false,
    val budgetAlertPercentage: Int = 0,
    val budgetPeriod: Instant = Clock.System.now(),
    val recurrent: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null,
    val synced: Boolean = false
)
