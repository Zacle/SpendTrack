package com.zacle.spendtrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.zacle.spendtrack.feature.budget.add_edit_budget.addEditBudgetScreen
import com.zacle.spendtrack.feature.budget.add_edit_budget.navigateToAddEditBudget
import com.zacle.spendtrack.feature.budget.budgets.budgetsScreen
import com.zacle.spendtrack.feature.budget.view_budget.budgetDetailScreen
import com.zacle.spendtrack.feature.budget.view_budget.navigateToBudgetDetail
import com.zacle.spendtrack.feature.expense.add_edit_expense.addEditExpenseScreen
import com.zacle.spendtrack.feature.expense.add_edit_expense.navigateToAddEditExpense
import com.zacle.spendtrack.feature.expense.view_expense.expenseDetailScreen
import com.zacle.spendtrack.feature.expense.view_expense.navigateToExpenseDetail
import com.zacle.spendtrack.feature.forgot_password.forgotPasswordScreen
import com.zacle.spendtrack.feature.forgot_password.navigateToForgotPassword
import com.zacle.spendtrack.feature.home.Home
import com.zacle.spendtrack.feature.home.homeScreen
import com.zacle.spendtrack.feature.home.navigateToHome
import com.zacle.spendtrack.feature.income.add_edit_income.addEditIncomeScreen
import com.zacle.spendtrack.feature.income.add_edit_income.navigateToAddEditIncome
import com.zacle.spendtrack.feature.income.view_income.incomeDetailScreen
import com.zacle.spendtrack.feature.income.view_income.navigateToIncomeDetail
import com.zacle.spendtrack.feature.login.loginScreen
import com.zacle.spendtrack.feature.login.navigateToLogin
import com.zacle.spendtrack.feature.onboarding.navigation.Onboarding
import com.zacle.spendtrack.feature.onboarding.navigation.onboardingScreen
import com.zacle.spendtrack.feature.register.navigateToRegister
import com.zacle.spendtrack.feature.register.registerScreen
import com.zacle.spendtrack.feature.verify_auth.navigateToVerifyAuth
import com.zacle.spendtrack.feature.verify_auth.verifyAuthScreen
import com.zacle.spendtrack.ui.STAppState

@Composable
fun STNavHost(
    isOffline: Boolean,
    appState: STAppState,
    modifier: Modifier = Modifier,
    startDestination: Any = Onboarding
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        onboardingScreen(
            navigateToLogin = navController::navigateToLogin
        )
        loginScreen(
            navigateToRegister = navController::navigateToRegister,
            navigateToForgotPassword = navController::navigateToForgotPassword,
            navigateToVerifyEmail = navController::navigateToVerifyAuth,
            navigateToHome = navController::navigateToHome,
            isOnline = !isOffline
        )
        registerScreen(
            isOffline = isOffline,
            navigateUp = navController::navigateUp,
            navigateToVerifyEmail = navController::navigateToVerifyAuth,
            navigateToHome = navController::navigateToHome,
            navigateToLogin = navController::navigateToLogin
        )
        forgotPasswordScreen(
            isOffline = isOffline,
            navigateUp = navController::navigateUp,
            navigateToLogin = navController::navigateToLogin
        )
        verifyAuthScreen(
            isOffline = isOffline,
            navigateUp = navController::navigateUp,
            navigateToHome = navController::navigateToHome
        )
        homeScreen(
            navigateToProfile = {},
            navigateToBudgets = {},
            navigateToTransactions = {},
            navigateToExpense = navController::navigateToExpenseDetail,
            navigateToIncome = navController::navigateToIncomeDetail,
            navigateToBudgetDetails = navController::navigateToBudgetDetail,
            navigateToLogin = navController::navigateToLogin
        )
        addEditExpenseScreen(
            navigateBack = navController::navigateUp,
            navigateToLogin = navController::navigateToHome
        )
        addEditIncomeScreen(
            navigateBack = navController::navigateUp,
            navigateToLogin = navController::navigateToHome
        )
        expenseDetailScreen(
            navigateBack = navController::navigateUp,
            navigateToLogin = navController::navigateToLogin,
            navigateToEditExpense = {
                navController.navigateToAddEditExpense(
                    expenseId = it,
                    navOptionsBuilder = {
                        popUpTo<Home> {
                            inclusive = false
                        }
                    }
                )
            }
        )
        incomeDetailScreen(
            onNavigateBack = navController::navigateUp,
            navigateToLogin = navController::navigateToLogin,
            navigateToEditIncome = {
                navController.navigateToAddEditIncome(
                    incomeId = it,
                    navOptionsBuilder = {
                        popUpTo<Home> {
                            inclusive = false
                        }
                    }
                )
            }
        )
        budgetsScreen(
            navigateToBudgetDetails = navController::navigateToBudgetDetail,
            navigateToLogin = navController::navigateToLogin,
            navigateToCreateBudget = navController::navigateToAddEditBudget
        )
        addEditBudgetScreen(
            navigateBack = navController::navigateUp,
            navigateToLogin = navController::navigateToLogin
        )
        budgetDetailScreen(
            navigateBack = navController::navigateUp,
            navigateToLogin = navController::navigateToLogin,
            onEditBudgetClick = navController::navigateToAddEditBudget,
            onExpenseClick = navController::navigateToExpenseDetail,
            onIncomeClick = navController::navigateToIncomeDetail
        )
    }
}