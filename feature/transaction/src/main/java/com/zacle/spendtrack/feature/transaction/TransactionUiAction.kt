package com.zacle.spendtrack.feature.transaction

import com.zacle.spendtrack.core.ui.UiAction
import kotlinx.datetime.Instant

sealed class TransactionUiAction: UiAction {
    data object OnFilterTransactionPressed: TransactionUiAction()
    data object OnFilterTransactionDismissed: TransactionUiAction()
    data class OnFilterTransactionApplied(val transactionUiState: TransactionUiState): TransactionUiAction()
    data object OnMonthPeriodPressed: TransactionUiAction()
    data object OnMonthPeriodDismissed: TransactionUiAction()
    data class OnMonthPeriodApplied(val selectedMonth: Instant): TransactionUiAction()
    data class OnNavigateToExpense(val expenseId: String): TransactionUiAction()
    data class OnNavigateToIncome(val incomeId: String): TransactionUiAction()
    data class OnNavigateToFinancialReport(val month: Int, val year: Int): TransactionUiAction()
}