package com.zacle.spendtrack.feature.report

import com.zacle.spendtrack.feature.report.RecordTransactionType.EXPENSE
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class ReportUiState(
    val userId: String = "",
    val selectedPeriod: Instant = Clock.System.now(),
    val recordTransactionType: RecordTransactionType = EXPENSE,
    val chartType: ChartType = ChartType.LINE,
    val shouldShowTransactions: Boolean = true,
    val shouldShowSelectReportPeriodDialog: Boolean = false
)
