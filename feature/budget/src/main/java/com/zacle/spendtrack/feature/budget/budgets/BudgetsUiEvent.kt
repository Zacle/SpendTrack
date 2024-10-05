package com.zacle.spendtrack.feature.budget.budgets

import com.zacle.spendtrack.core.ui.UiEvent

sealed class BudgetsUiEvent: UiEvent {
    data object NavigateToCreateBudget: BudgetsUiEvent()
    data class NavigateToBudgetDetails(val budgetId: String): BudgetsUiEvent()
    data object NavigateToLogin: BudgetsUiEvent()
}