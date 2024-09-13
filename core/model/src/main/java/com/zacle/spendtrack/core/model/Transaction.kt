package com.zacle.spendtrack.core.model

import kotlinx.datetime.Instant

interface Transaction {
    val id: String
    val userId: String
    val name: String
    val description: String
    val amount: Double
    val transactionDate: Instant
    val category: Category
    val receiptUrl: String?
    val updatedAt: Instant?
    val synced: Boolean
}