package com.zacle.spendtrack.feature.budget.view_budget

import com.zacle.spendtrack.core.ui.UiEvent

sealed class BudgetDetailUiEvent: UiEvent {
    data object NavigateToLogin: BudgetDetailUiEvent()
    data class NavigateToEditBudget(val budgetId: String): BudgetDetailUiEvent()
    data class NavigateToExpenseDetail(val expenseId: String): BudgetDetailUiEvent()
    data class NavigateToIncomeDetail(val incomeId: String): BudgetDetailUiEvent()
    data object NavigateBack: BudgetDetailUiEvent()
}