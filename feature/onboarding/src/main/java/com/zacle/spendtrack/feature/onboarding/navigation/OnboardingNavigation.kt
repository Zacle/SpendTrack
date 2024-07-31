package com.zacle.spendtrack.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zacle.spendtrack.feature.onboarding.OnboardingRoute
import kotlinx.serialization.Serializable

@Serializable
data object Onboarding

fun NavGraphBuilder.onboardingScreen(
    navigateToLogin: () -> Unit
) {
    composable<Onboarding> {
        OnboardingRoute(navigateToLogin = navigateToLogin)
    }
}