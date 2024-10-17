package com.zacle.spendtrack.feature.report

import com.zacle.spendtrack.core.domain.CategoryStats
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.Transaction

data class ReportModel(
    val totalMonthlyExpenses: Int,
    val totalMonthlyIncomes: Int,
    val transactions: List<Transaction>,
    val expensesReport: Map<Int, Int>,
    val incomesReport: Map<Int, Int>,
    val categoryExpensesReport: Map<Category, CategoryStats>,
    val categoryIncomesReport: Map<Category, CategoryStats>
)
