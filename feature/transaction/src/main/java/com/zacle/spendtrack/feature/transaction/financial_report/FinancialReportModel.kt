package com.zacle.spendtrack.feature.transaction.financial_report

import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Transaction

data class FinancialReportModel(
    val amountSpent: Double,
    val amountEarned: Double,
    val budgetsSize: Int,
    val biggestExpense: Transaction?,
    val biggestIncome: Transaction?,
    val exceedingBudgets: List<Budget>,
)
