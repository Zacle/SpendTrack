package com.zacle.spendtrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.zacle.spendtrack.feature.forgot_password.forgotPasswordScreen
import com.zacle.spendtrack.feature.forgot_password.navigateToForgotPassword
import com.zacle.spendtrack.feature.home.homeScreen
import com.zacle.spendtrack.feature.home.navigateToHome
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
            navigateToHome = navController::navigateToHome
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
            navigateToExpense = {},
            navigateToIncome = {},
            navigateToBudgetDetails = {},
            navigateToLogin = navController::navigateToLogin
        )
    }
}