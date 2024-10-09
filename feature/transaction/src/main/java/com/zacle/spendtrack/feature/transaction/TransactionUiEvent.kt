package com.zacle.spendtrack.feature.transaction

import com.zacle.spendtrack.core.ui.UiEvent

sealed class TransactionUiEvent: UiEvent {
    data class NavigateToExpense(val expenseId: String): TransactionUiEvent()
    data class NavigateToIncome(val incomeId: String): TransactionUiEvent()
    data class NavigateToFinancialReport(val month: Int, val year: Int): TransactionUiEvent()
    data object NavigateToLogin: TransactionUiEvent()
}