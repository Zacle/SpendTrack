package com.zacle.spendtrack.feature.profile.edit_profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Profile

fun NavController.navigateToProfile(
    navOptions: NavOptions? = null
) {
    navigate(Profile, navOptions)
}

fun NavGraphBuilder.profileScreen(
    navigateToLogin: () -> Unit,
    navigateUp: () -> Unit
) {
    composable<Profile>() {
        ProfileRoute(
            navigateToLogin = navigateToLogin,
            navigateUp = navigateUp
        )
    }
}