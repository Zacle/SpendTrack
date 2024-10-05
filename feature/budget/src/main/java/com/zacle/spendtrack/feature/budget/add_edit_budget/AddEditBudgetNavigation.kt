package com.zacle.spendtrack.feature.budget.add_edit_budget

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

const val BUDGET_ID_ARG = "budgetId"

@Serializable
data class AddEditBudget(
    val budgetId: String? = null,
)

fun NavController.navigateToAddEditBudget(
    budgetId: String? = null,
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(AddEditBudget(budgetId), navOptionsBuilder)
}

fun NavGraphBuilder.addEditBudgetScreen(
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit
) {
    composable<AddEditBudget> {
        AddEditBudgetRoute(
            navigateBack = navigateBack,
            navigateToLogin = navigateToLogin
        )
    }
}