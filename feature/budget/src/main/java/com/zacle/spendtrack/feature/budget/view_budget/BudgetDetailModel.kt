package com.zacle.spendtrack.feature.budget.view_budget

import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Transaction

data class BudgetDetailModel(
    val budget: Budget,
    val transactions: List<Transaction>
)
