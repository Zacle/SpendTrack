package com.zacle.spendtrack.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Preferences

fun NavController.navigateToPreferences(
    navOptions: NavOptions? = null
) {
    navigate(Preferences, navOptions)
}

fun NavGraphBuilder.preferencesScreen(
    navigateToLogin: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateBack: () -> Unit
) {
    composable<Preferences> {
        PreferencesRoute(
            navigateToLogin = navigateToLogin,
            navigateToEditProfile = navigateToEditProfile,
            onBackClick = navigateBack
        )
    }
}