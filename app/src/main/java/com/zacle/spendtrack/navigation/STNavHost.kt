package com.zacle.spendtrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.zacle.spendtrack.feature.onboarding.navigation.Onboarding
import com.zacle.spendtrack.feature.onboarding.navigation.onboardingScreen
import com.zacle.spendtrack.ui.STAppState

@Composable
fun STNavHost(
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
            navigateToLogin = { /* TODO: Handle login navigation */ }
        )
    }
}