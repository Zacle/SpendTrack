package com.zacle.spendtrack.feature.report

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Report

fun NavController.navigateToReport(
    navOptions: NavOptions? = null
) {
    navigate(Report, navOptions)
}

fun NavGraphBuilder.reportScreen(
    navigateToLogin: () -> Unit,
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit
) {
    composable<Report> {
        ReportRoute(
            navigateToLogin = navigateToLogin,
            navigateToExpense = navigateToExpense,
            navigateToIncome = navigateToIncome
        )
    }
}
