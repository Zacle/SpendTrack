package com.zacle.spendtrack.feature.transaction.financial_report

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

const val MONTH_ARG_KEY = "month"
const val YEAR_ARG_KEY = "year"

@Serializable
data class FinancialReport(
    val month: Int,
    val year: Int
)

fun NavController.navigateToFinancialReport(
    month: Int,
    year: Int,
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(FinancialReport(month, year), navOptionsBuilder)
}

fun NavGraphBuilder.financialReportScreen(
    navigateUp: () -> Unit,
    navigateToLogin: () -> Unit
) {
    composable<FinancialReport> {
        FinancialReportRoute(
            navigateUp = navigateUp,
            navigateToLogin = navigateToLogin
        )
    }
}