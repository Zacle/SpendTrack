package com.zacle.spendtrack.feature.transaction.financial_report

import com.zacle.spendtrack.core.ui.UiEvent

sealed class FinancialReportUiEvent: UiEvent {
    data object NavigateBack: FinancialReportUiEvent()
    data object NavigateToLogin: FinancialReportUiEvent()
}