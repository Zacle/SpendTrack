package com.zacle.spendtrack.feature.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Login

fun NavController.navigateToLogin(
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {
        launchSingleTop = true
        popUpTo(0) {  inclusive = true }
    }
) {
    navigate(Login, navOptionsBuilder)
}

fun NavGraphBuilder.loginScreen(
    navigateToRegister: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    navigateToVerifyEmail: () -> Unit,
    navigateToHome: () -> Unit,
    isOnline: Boolean = false
) {
    composable<Login> {
        LoginRoute(
            isOnline = isOnline,
            navigateToRegister = navigateToRegister,
            navigateToForgotPassword = navigateToForgotPassword,
            navigateToVerifyEmail = navigateToVerifyEmail,
            navigateToHome = navigateToHome
        )
    }
}