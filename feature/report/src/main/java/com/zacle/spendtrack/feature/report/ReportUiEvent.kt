package com.zacle.spendtrack.feature.report

import com.zacle.spendtrack.core.ui.UiEvent

sealed class ReportUiEvent: UiEvent {
    data class NavigateToExpense(val expenseId: String): ReportUiEvent()
    data class NavigateToIncome(val incomeId: String): ReportUiEvent()
    data object NavigateToLogin: ReportUiEvent()
}