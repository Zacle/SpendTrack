package com.zacle.spendtrack.feature.expense.add_edit_expense

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

const val EXPENSE_ID_ARG: String = "expenseId"

@Serializable
data class AddEditExpense(
    val expenseId: String? = null
)

fun NavController.navigateToAddEditExpense(
    expenseId: String? = null,
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(AddEditExpense(expenseId), navOptionsBuilder)
}

fun NavGraphBuilder.addEditExpenseScreen(
    navigateBack: () -> Unit
) {
    composable<AddEditExpense> {
        // TODO: Add edit expense screen
    }
}