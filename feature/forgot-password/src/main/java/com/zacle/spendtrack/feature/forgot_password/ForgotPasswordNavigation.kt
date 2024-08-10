package com.zacle.spendtrack.feature.forgot_password

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object ForgotPassword

fun NavController.navigateToForgotPassword(navOptions: NavOptions? = null) {
    navigate(ForgotPassword, navOptions)
}

fun NavGraphBuilder.forgotPasswordScreen(
    isOffline: Boolean,
    navigateToLogin: () -> Unit,
    navigateUp: () -> Unit
) {
    composable<ForgotPassword> {
        ForgotPasswordRoute(
            isOffline = isOffline,
            navigateToLogin = navigateToLogin,
            navigateUp = navigateUp
        )
    }
}