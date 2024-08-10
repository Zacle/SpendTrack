package com.zacle.spendtrack.feature.register

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Register

fun NavController.navigateToRegister(navOptions: NavOptions? = null) {
    navigate(Register, navOptions)
}

fun NavGraphBuilder.registerScreen(
    isOffline: Boolean,
    navigateToLogin: () -> Unit,
    navigateToVerifyEmail: () -> Unit,
    navigateToHome: () -> Unit,
    navigateUp: () -> Unit
) {
    composable<Register> {
        RegisterRoute(
            isOffline = isOffline,
            navigateToLogin = navigateToLogin,
            navigateToVerifyEmail = navigateToVerifyEmail,
            navigateToHome = navigateToHome,
            navigateUp = navigateUp
        )
    }
}