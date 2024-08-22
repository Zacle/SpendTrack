package com.zacle.spendtrack.core.model

import kotlinx.datetime.Instant

interface Transaction {
    val amount: Double
    val transactionDate: Instant
    val category: Category
}