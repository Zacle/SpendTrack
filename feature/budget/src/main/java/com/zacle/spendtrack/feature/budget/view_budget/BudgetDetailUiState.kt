package com.zacle.spendtrack.feature.budget.view_budget

data class BudgetDetailUiState(
    val userId: String = "",
    val shouldDisplayRemoveBudgetDialog: Boolean = false,
    val currencyCode: String = "USD"
)
