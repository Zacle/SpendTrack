package com.zacle.spendtrack.core.domain.util

import com.zacle.spendtrack.core.model.Period

data class FilterState(
    val includeIncomes: Boolean = true,
    val includeExpenses: Boolean = true,
    val categoryIds: Set<String> = emptySet(),
    val period: Period = Period()
)
