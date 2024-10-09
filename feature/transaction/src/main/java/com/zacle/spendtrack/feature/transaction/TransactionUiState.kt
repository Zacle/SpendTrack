package com.zacle.spendtrack.feature.transaction

import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.util.FilterState
import com.zacle.spendtrack.core.model.util.SortOrder
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class TransactionUiState(
    val userId: String = "",
    val numberOfTransactionsSelected: Int = 0,
    val selectedMonth: Instant = Clock.System.now(),
    val sortOrder: SortOrder = SortOrder.NEWEST,
    val filterState: FilterState = FilterState(),
    val shouldDisplayFilterTransaction: Boolean = false,
    val shouldDisplayMonthPeriod: Boolean = false,
    val categories: List<Category> = emptyList()
)
