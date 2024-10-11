package com.zacle.spendtrack.feature.transaction.financial_report

import com.zacle.spendtrack.core.ui.UiAction

sealed class FinancialReportUiAction: UiAction {
    data object Load: FinancialReportUiAction()
}