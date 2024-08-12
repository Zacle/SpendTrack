package com.zacle.spendtrack.feature.verify_auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.SpendTrackBackground
import com.zacle.spendtrack.core.designsystem.component.SpendTrackButton
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.ui.previews.DevicePreviews
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VerifyAuthRoute(
    isOffline: Boolean,
    navigateUp: () -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VerifyAuthViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val emailNotVerified = stringResource(id = R.string.email_not_verified)

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collectLatest { event ->
            when (event) {
                is VerifyAuthUiEvent.NotVerifiedYet -> {
                    snackbarHostState.showSnackbar(emailNotVerified)
                }
                is VerifyAuthUiEvent.NavigateToHome -> {
                    navigateToHome()
                }
            }
        }
    }

    VerifyAuthScreen(
        isOffline = isOffline,
        snackbarHostState = snackbarHostState,
        onAlreadyVerifiedClicked = { viewModel.submitAction(VerifyAuthUiAction.OnAlreadyVerifiedClicked) },
        navigateUp = navigateUp,
        modifier = modifier
    )
}

@Composable
fun VerifyAuthScreen(
    isOffline: Boolean,
    snackbarHostState: SnackbarHostState,
    onAlreadyVerifiedClicked: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            STTopAppBar(
                titleRes = R.string.title,
                navigationIcon = {
                    Icon(
                        imageVector = SpendTrackIcons.arrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { navigateUp() }
                    )
                }
            )
        },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        VerifyAuthContent(
            isOffline = isOffline,
            onAlreadyVerifiedClicked = onAlreadyVerifiedClicked,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun VerifyAuthContent(
    isOffline: Boolean,
    onAlreadyVerifiedClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.email_verification_text),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 64.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        SpendTrackButton(
            text = stringResource(id = R.string.already_verified),
            onClick = onAlreadyVerifiedClicked,
            enabled = !isOffline
        )
    }
}

@DevicePreviews
@Composable
fun VerifyAuthContentPreview() {
    SpendTrackTheme {
        SpendTrackBackground {
            VerifyAuthContent(
                isOffline = false,
                onAlreadyVerifiedClicked = {}
            )
        }
    }
}