package com.zacle.spendtrack.feature.register

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zacle.spendtrack.core.designsystem.component.GoogleButton
import com.zacle.spendtrack.core.designsystem.component.STOutlinedTextField
import com.zacle.spendtrack.core.designsystem.component.STPasswordTextField
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.SpendTrackBackground
import com.zacle.spendtrack.core.designsystem.component.SpendTrackButton
import com.zacle.spendtrack.core.designsystem.component.TOP_APP_BAR_PADDING
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.ui.previews.DevicePreviews
import com.zacle.spendtrack.core.ui.types.NameError
import com.zacle.spendtrack.core.ui.types.PasswordError
import kotlinx.coroutines.flow.collectLatest
import com.zacle.spendtrack.core.shared_resources.R as SharedR

@Composable
fun RegisterRoute(
    isOffline: Boolean,
    navigateToLogin: () -> Unit,
    navigateToVerifyEmail: () -> Unit,
    navigateToHome: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val registrationFailed = stringResource(id = SharedR.string.registration_failed)

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collectLatest { event ->
            when (event) {
                is RegisterUiEvent.RegistrationFailed -> {
                    snackbarHostState.showSnackbar(registrationFailed)
                }
                is RegisterUiEvent.NavigateToVerifyEmail -> {
                    navigateToVerifyEmail()
                }
                is RegisterUiEvent.NavigateToLogin -> {
                    navigateToLogin()
                }
                is RegisterUiEvent.NavigateToHome -> {
                    navigateToHome()
                }
            }
        }
    }

    RegisterScreen(
        uiState = uiState,
        isOffline = isOffline,
        snackbarHostState = snackbarHostState,
        onFirstNameChanged = { viewModel.submitAction(RegisterUiAction.OnFirstNameChanged(it)) },
        onLastNameChanged = { viewModel.submitAction(RegisterUiAction.OnLastNameChanged(it)) },
        onEmailChanged = { viewModel.submitAction(RegisterUiAction.OnEmailChanged(it)) },
        onPasswordChanged = { viewModel.submitAction(RegisterUiAction.OnPasswordChanged(it)) },
        onRegisterClicked = { viewModel.submitAction(RegisterUiAction.OnRegisterClicked) },
        onGoogleSignInClicked = { viewModel.submitAction(RegisterUiAction.OnGoogleSignInClicked(it)) },
        onLoginClicked = { viewModel.submitAction(RegisterUiAction.OnLoginClicked) },
        navigateUp = navigateUp,
        modifier = modifier
    )
}

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    isOffline: Boolean,
    snackbarHostState: SnackbarHostState,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRegisterClicked: () -> Unit,
    onGoogleSignInClicked: (Context) -> Unit,
    onLoginClicked: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            STTopAppBar(
                title = {
                    Text(text = stringResource(id = SharedR.string.register))
                },
                navigationIcon = {
                    Icon(
                        imageVector = SpendTrackIcons.arrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { navigateUp() }
                    )
                }
            )
        },
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val contentPadding = Modifier.padding(innerPadding)
        RegisterContent(
            uiState = uiState,
            isOffline = isOffline,
            onFirstNameChanged = onFirstNameChanged,
            onLastNameChanged = onLastNameChanged,
            onEmailChanged = onEmailChanged,
            onPasswordChanged = onPasswordChanged,
            onRegisterClicked = onRegisterClicked,
            onGoogleSignInClicked = onGoogleSignInClicked,
            onLoginClicked = onLoginClicked,
            modifier = contentPadding
        )
    }
}

@Composable
fun RegisterContent(
    uiState: RegisterUiState,
    isOffline: Boolean,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRegisterClicked: () -> Unit,
    onGoogleSignInClicked: (Context) -> Unit,
    onLoginClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(top = TOP_APP_BAR_PADDING.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RegisterForm(
            uiState = uiState,
            isOffline = isOffline,
            onFirstNameChanged = onFirstNameChanged,
            onLastNameChanged = onLastNameChanged,
            onEmailChanged = onEmailChanged,
            onPasswordChanged = onPasswordChanged,
            onRegisterClicked = onRegisterClicked
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = SharedR.string.or),
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            GoogleButton(
                onClick = { onGoogleSignInClicked(context) },
                modifier = Modifier.padding(horizontal = 16.dp),
                enabled = !isOffline
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(id = SharedR.string.have_account))
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(id = SharedR.string.login),
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onLoginClicked() }
            )
        }
    }
}

@Composable
fun RegisterForm(
    uiState: RegisterUiState,
    isOffline: Boolean,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRegisterClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        STOutlinedTextField(
            name = uiState.firstName,
            placeholder = stringResource(id = SharedR.string.first_name),
            onValueChange = { onFirstNameChanged(it) },
            errorResId = uiState.firstNameError?.errorMessageResId
        )
        STOutlinedTextField(
            name = uiState.lastName,
            placeholder = stringResource(id = SharedR.string.last_name),
            onValueChange = { onLastNameChanged(it) },
            errorResId = uiState.lastNameError?.errorMessageResId
        )
        STOutlinedTextField(
            name = uiState.email,
            placeholder = stringResource(id = SharedR.string.email),
            onValueChange = { onEmailChanged(it) },
            errorResId = uiState.emailError?.errorMessageResId
        )
        STPasswordTextField(
            password = uiState.password,
            placeholder = stringResource(id = SharedR.string.password),
            onValueChange = { onPasswordChanged(it) },
            errorResId = uiState.passwordError?.errorMessageResId
        )
        Spacer(modifier = Modifier.height(12.dp))
        SpendTrackButton(
            text = stringResource(id = SharedR.string.register),
            onClick = onRegisterClicked,
            enabled = !isOffline
        )
    }
}

@DevicePreviews
@Composable
fun RegisterContentPreview() {
    SpendTrackTheme {
        SpendTrackBackground {
            RegisterContent(
                uiState = RegisterUiState(
                    passwordError = PasswordError.SHORT,
                    firstNameError = NameError.BLANK
                ),
                isOffline = false,
                onFirstNameChanged = {},
                onLastNameChanged = {},
                onEmailChanged = {},
                onPasswordChanged = {},
                onRegisterClicked = {},
                onGoogleSignInClicked = {},
                onLoginClicked = {}
            )
        }
    }
}