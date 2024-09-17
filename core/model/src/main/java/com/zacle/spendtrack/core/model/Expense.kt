package com.zacle.spendtrack.core.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

data class Expense(
    override val id: String = UUID.randomUUID().toString(),
    override val userId: String = "",
    override val name: String = "",
    override val description: String = "",
    override val amount: Double = 0.0,
    override val category: Category = Category(),
    override val transactionDate: Instant = Clock.System.now(),
    override val receiptUrl: String? = null,
    override val localReceiptImagePath: String? = null,
    override val updatedAt: Instant? = null,
    override val synced: Boolean = false
): Transaction
