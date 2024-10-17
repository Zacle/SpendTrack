package com.zacle.spendtrack.feature.report

import com.zacle.spendtrack.core.ui.UiAction
import kotlinx.datetime.Instant

sealed class ReportUiAction: UiAction {
    data object OnReportPeriodPressed: ReportUiAction()
    data object OnReportPeriodDismissed: ReportUiAction()
    data class OnReportPeriodConfirmed(val date: Instant): ReportUiAction()
    data class OnRecordTransactionTypeChanged(val type: RecordTransactionType): ReportUiAction()
    data class OnChartTypeChanged(val type: ChartType): ReportUiAction()
    data class OnShowTransactions(val showTransactions: Boolean): ReportUiAction()
    data class OnNavigateToExpense(val expenseId: String): ReportUiAction()
    data class OnNavigateToIncome(val incomeId: String): ReportUiAction()
}