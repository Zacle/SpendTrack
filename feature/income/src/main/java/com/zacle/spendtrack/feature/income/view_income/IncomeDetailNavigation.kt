package com.zacle.spendtrack.feature.income.view_income

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class IncomeDetail(
    val incomeId: String
)

fun NavController.navigateToIncomeDetail(
    incomeId: String,
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(IncomeDetail(incomeId), navOptionsBuilder)
}

fun NavGraphBuilder.incomeDetailScreen(
    onNavigateBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToEditIncome: (String) -> Unit
) {
    composable<IncomeDetail> {
        IncomeDetailRoute(
            navigateBack = onNavigateBack,
            navigateToLogin = navigateToLogin,
            navigateToEditIncome = navigateToEditIncome
        )
    }
}