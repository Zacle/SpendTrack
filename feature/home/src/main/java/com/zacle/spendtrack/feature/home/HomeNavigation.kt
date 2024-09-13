package com.zacle.spendtrack.feature.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Home

fun NavController.navigateToHome(
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {
        launchSingleTop = true
        popUpTo(0)
    }
) {
    navigate(Home, navOptionsBuilder)
}

fun NavGraphBuilder.homeScreen(
    navigateToProfile: () -> Unit,
    navigateToBudgets: () -> Unit,
    navigateToTransactions: () -> Unit,
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit,
    navigateToBudgetDetails: (String) -> Unit,
    navigateToLogin: () -> Unit
) {
    composable<Home> {
        HomeRoute(
            navigateToProfile = navigateToProfile,
            navigateToBudgets = navigateToBudgets,
            navigateToTransactions = navigateToTransactions,
            navigateToExpense = navigateToExpense,
            navigateToIncome = navigateToIncome,
            navigateToBudgetDetails = navigateToBudgetDetails,
            navigateToLogin = navigateToLogin
        )
    }
}