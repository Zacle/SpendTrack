package com.zacle.spendtrack.core.model.util

data class FilterState(
    val includeIncomes: Boolean = true,
    val includeExpenses: Boolean = true,
    val categoryIds: Set<String> = emptySet(),
)