package com.zacle.spendtrack.core.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Income(
    val incomeId: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val receiptUrl: String? = null,
    val updatedAt: Instant? = null,
    val synced: Boolean = false,
    override val amount: Double = 0.0,
    override val category : Category = Category(),
    override val transactionDate: Instant = Clock.System.now()
): Transaction
