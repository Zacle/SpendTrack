package com.zacle.spendtrack.feature.forgot_password

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zacle.spendtrack.core.designsystem.component.STOutlinedTextField
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.SpendTrackBackground
import com.zacle.spendtrack.core.designsystem.component.SpendTrackButton
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.ui.previews.DevicePreviews

@Composable
fun ForgotPasswordRoute(
    isOffline: Boolean,
    navigateUp: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val emailNotFound = stringResource(id = R.string.email_not_found)

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect { event ->
            when (event) {
                is ForgotPasswordUiEvent.EmailNotFound -> {
                    snackbarHostState.showSnackbar(emailNotFound)
                }
                is ForgotPasswordUiEvent.NavigateToLogin -> {
                    navigateToLogin()
                }
            }
        }
    }

    ForgotPasswordScreen(
        uiState = uiState,
        isOffline = isOffline,
        onEmailChanged = { viewModel.submitAction(ForgotPasswordUiAction.OnEmailChanged(it)) },
        onResetPasswordClicked = { viewModel.submitAction(ForgotPasswordUiAction.OnSubmitClicked) },
        navigateUp = navigateUp,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun ForgotPasswordScreen(
    uiState: ForgotPasswordUiState,
    isOffline: Boolean,
    snackbarHostState: SnackbarHostState,
    onEmailChanged: (String) -> Unit,
    onResetPasswordClicked: () -> Unit,
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
        ForgotPasswordContent(
            uiState = uiState,
            isOffline = isOffline,
            onEmailChanged = onEmailChanged,
            onResetPasswordClicked = onResetPasswordClicked,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ForgotPasswordContent(
    uiState: ForgotPasswordUiState,
    isOffline: Boolean,
    onEmailChanged: (String) -> Unit,
    onResetPasswordClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.reset_password_text),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 64.dp)
        )
        STOutlinedTextField(
            name = uiState.email,
            placeholder = stringResource(id = R.string.email),
            onValueChange = { onEmailChanged(it) },
            errorResId = uiState.emailError?.errorMessageResId
        )
        SpendTrackButton(
            text = stringResource(id = R.string.submit),
            onClick = onResetPasswordClicked,
            enabled = !isOffline
        )
    }
}

@DevicePreviews
@Composable
fun ForgotPasswordContentPreview() {
    SpendTrackTheme {
        SpendTrackBackground {
            ForgotPasswordContent(
                uiState = ForgotPasswordUiState(),
                isOffline = false,
                onEmailChanged = {},
                onResetPasswordClicked = {}
            )
        }
    }
}