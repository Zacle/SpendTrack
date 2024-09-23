package com.zacle.spendtrack.feature.income.add_edit_income

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

const val INCOME_ID_ARG = "incomeId"

@Serializable
data class AddEditIncome(
    val incomeId: String? = null
)

fun NavController.navigateToAddEditIncome(
    incomeId: String? = null,
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(AddEditIncome(incomeId), navOptionsBuilder)
}

fun NavGraphBuilder.addEditIncomeScreen(
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit
) {
    composable<AddEditIncome> {
        AddEditIncomeRoute(
            navigateBack = navigateBack,
            navigateToLogin = navigateToLogin
        )
    }
}