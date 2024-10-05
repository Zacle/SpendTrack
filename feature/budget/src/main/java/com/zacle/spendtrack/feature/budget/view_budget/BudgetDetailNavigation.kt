package com.zacle.spendtrack.feature.budget.view_budget

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class BudgetDetail(
    val budgetId: String
)

fun NavController.navigateToBudgetDetail(
    budgetId: String,
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(BudgetDetail(budgetId), navOptionsBuilder)
}

fun NavGraphBuilder.budgetDetailScreen(
    navigateBack: () -> Unit,
    onEditBudgetClick: (String) -> Unit,
    onExpenseClick: (String) -> Unit,
    onIncomeClick: (String) -> Unit,
    navigateToLogin: () -> Unit,
) {
    composable<BudgetDetail> {
        BudgetDetailRoute(
            onBackClick = navigateBack,
            onEditBudgetClick = onEditBudgetClick,
            onExpenseClick = onExpenseClick,
            onIncomeClick = onIncomeClick,
            navigateToLogin = navigateToLogin
        )
    }
}