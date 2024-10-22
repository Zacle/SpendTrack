package com.zacle.spendtrack.feature.budget.budgets

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class BudgetsUiState(
    val userId: String = "",
    val selectedMonth: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val currencyCode: String = ""
)