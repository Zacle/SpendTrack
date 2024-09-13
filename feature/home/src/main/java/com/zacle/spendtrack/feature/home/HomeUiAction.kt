package com.zacle.spendtrack.feature.home

import com.zacle.spendtrack.core.ui.UiAction
import kotlinx.datetime.Instant

sealed class HomeUiAction: UiAction {
    data class SetPeriod(val date: Instant): HomeUiAction()
    data class SetDisplayTransactions(val shouldDisplayTransactions: Boolean): HomeUiAction()
    data class SetFilterPeriod(val transactionPeriodType: TransactionPeriodType): HomeUiAction()
    data class NavigateToExpense(val expenseId: String): HomeUiAction()
    data class NavigateToIncome(val incomeId: String): HomeUiAction()
    data class NavigateToBudgetDetails(val budgetId: String): HomeUiAction()
    data object Load: HomeUiAction()
    data object NavigateToProfile: HomeUiAction()
    data object NavigateToBudgets: HomeUiAction()
    data object NavigateToTransactions: HomeUiAction()
}