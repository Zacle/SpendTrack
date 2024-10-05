package com.zacle.spendtrack.feature.budget.budgets

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Budgets

fun NavController.navigateToBudgets(
    navOptions: NavOptions? = null
) {
    navigate(Budgets, navOptions)
}

fun NavGraphBuilder.budgetsScreen(
    navigateToBudgetDetails: (String) -> Unit,
    navigateToCreateBudget: () -> Unit,
    navigateToLogin: () -> Unit
) {
    composable<Budgets> {
        BudgetsRoute(
            navigateToBudgetDetails = navigateToBudgetDetails,
            navigateToCreateBudget = navigateToCreateBudget,
            navigateToLogin = navigateToLogin
        )
    }
}