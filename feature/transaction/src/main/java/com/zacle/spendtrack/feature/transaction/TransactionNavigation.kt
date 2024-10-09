package com.zacle.spendtrack.feature.transaction

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Transaction

fun NavController.navigateToTransaction(
    navOptions: NavOptions? = null
) {
    navigate(Transaction, navOptions)
}

fun NavGraphBuilder.transactionScreen(
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit,
    navigateToFinancialReport: (Int, Int) -> Unit,
    navigateToLogin: () -> Unit
) {
    composable<Transaction> {
        TransactionRoute(
            navigateToExpense = navigateToExpense,
            navigateToIncome = navigateToIncome,
            navigateToFinancialReport = navigateToFinancialReport,
            navigateToLogin = navigateToLogin
        )
    }
}