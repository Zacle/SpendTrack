package com.zacle.spendtrack.feature.home

import com.zacle.spendtrack.core.ui.UiEvent

sealed class HomeUiEvent: UiEvent {
    data object NavigateToProfile: HomeUiEvent()
    data object NavigateToBudgets: HomeUiEvent()
    data object NavigateToTransactions: HomeUiEvent()
    data class NavigateToExpense(val expenseId: String): HomeUiEvent()
    data class NavigateToIncome(val incomeId: String): HomeUiEvent()
    data class NavigateToBudgetDetails(val budgetId: String): HomeUiEvent()
    data object NavigateToLogin: HomeUiEvent()
}