package com.zacle.spendtrack.feature.expense.view_expense

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ExpenseDetail(
    val expenseId: String
)

fun NavController.navigateToExpenseDetail(
    expenseId: String,
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(ExpenseDetail(expenseId), navOptionsBuilder)
}

fun NavGraphBuilder.expenseDetailScreen(
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToEditExpense: (String) -> Unit
) {
    composable<ExpenseDetail> {
        ExpenseDetailRoute(
            navigateBack = navigateBack,
            navigateToLogin = navigateToLogin,
            navigateToEditExpense = navigateToEditExpense
        )
    }
}