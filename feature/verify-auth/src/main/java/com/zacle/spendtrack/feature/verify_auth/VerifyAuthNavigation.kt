package com.zacle.spendtrack.feature.verify_auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object VerifyAuth

fun NavController.navigateToVerifyAuth(navOptions: NavOptions? = null) {
    navigate(VerifyAuth, navOptions)
}

fun NavGraphBuilder.verifyAuthScreen(
    navigateUp: () -> Unit,
    onRestartApp: () -> Unit
) {
    composable<VerifyAuth> {
        VerifyAuthRoute(
            navigateUp = navigateUp,
            onRestartApp = onRestartApp
        )
    }
}