package com.zacle.spendtrack.feature.home

import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Transaction

data class HomeModel(
    val accountBalance: Double = 0.0,
    val amountSpent: Double = 0.0,
    val amountEarned: Double = 0.0,
    val transactions: List<Transaction> = emptyList(),
    val totalBudget: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val budgets: List<Budget> = emptyList(),
    val transactionsReport: Map<Int, Int>
)
