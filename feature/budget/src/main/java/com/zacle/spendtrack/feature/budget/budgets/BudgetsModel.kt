package com.zacle.spendtrack.feature.budget.budgets

import com.zacle.spendtrack.core.model.Budget

data class BudgetsModel(
    val totalBudget: Double,
    val remainingBudget: Double,
    val budgets: List<Budget>
)
