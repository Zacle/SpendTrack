package com.zacle.spendtrack.ui

import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zacle.spendtrack.R
import com.zacle.spendtrack.core.designsystem.component.SpendTrackBackground
import com.zacle.spendtrack.data.UserStateModel
import com.zacle.spendtrack.feature.onboarding.navigation.Onboarding
import com.zacle.spendtrack.navigation.STNavHost

@Composable
fun STApp(
    appState: STAppState,
    userStateModel: UserStateModel,
    modifier: Modifier = Modifier
) {
    SpendTrackBackground(modifier = modifier) {
        val snackbarHostState = remember { SnackbarHostState() }
        val isOffline by appState.isOffline.collectAsStateWithLifecycle()

        // If user is not connected to the internet show a snack bar to inform them
        val notConnectedMessage = stringResource(R.string.not_connected)
        LaunchedEffect(isOffline) {
            if (isOffline) {
                snackbarHostState.showSnackbar(
                    message = notConnectedMessage,
                    duration = Indefinite
                )
            }
        }
        // TODO Add login flow
        val startDestination: Any =
            if (!userStateModel.userData.shouldHideOnboarding)
                Onboarding
            else
                ""
        STNavHost(
            appState = appState,
            startDestination = startDestination
        )
    }
}