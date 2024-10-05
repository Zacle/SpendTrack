package com.zacle.spendtrack.feature.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import kotlinx.serialization.Serializable

@Serializable
data object Home

fun NavController.navigateToHome(
    navOptions: NavOptions = navOptions {
        launchSingleTop = true
        popUpTo(0)
    }
) {
    navigate(Home, navOptions)
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