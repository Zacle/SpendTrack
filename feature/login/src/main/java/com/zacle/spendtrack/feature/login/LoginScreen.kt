package com.zacle.spendtrack.feature.login

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.ui.previews.DevicePreviews
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginRoute(
    navigateToRegister: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToVerifyEmail: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val loginFailed = stringResource(id = R.string.login_failed)
    val invalidEmail = stringResource(id = R.string.invalid_email)
    val passwordIsBlank = stringResource(id = R.string.password_is_blank)

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collectLatest { event ->
            when (event) {
                is LoginUiEvent.LoginFailed -> {
                    snackbarHostState.showSnackbar(loginFailed)
                }
                is LoginUiEvent.InvalidEmail -> {
                    snackbarHostState.showSnackbar(invalidEmail)
                }
                is LoginUiEvent.PasswordIsBlank -> {
                    snackbarHostState.showSnackbar(passwordIsBlank)
                }
                is LoginUiEvent.NavigateToVerifyEmail -> {
                    navigateToVerifyEmail()
                }
                is LoginUiEvent.NavigateToRegister -> {
                    navigateToRegister()
                }
                is LoginUiEvent.NavigateToForgotPassword -> {
                    navigateToForgotPassword()
                }
                is LoginUiEvent.NavigateToHome -> {
                    navigateToHome()
                }
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        onEmailChanged = { viewModel.submitAction(LoginUiAction.OnEmailChanged(it)) },
        onPasswordChanged = { viewModel.submitAction(LoginUiAction.OnPasswordChanged(it)) },
        onLoginClicked = { viewModel.submitAction(LoginUiAction.OnLoginClicked) },
        onGoogleSignInClicked = { viewModel.submitAction(LoginUiAction.OnGoogleSignInClicked(it)) },
        onRegisterClicked = { viewModel.submitAction(LoginUiAction.OnRegisterClicked) },
        onForgotPasswordClicked = { viewModel.submitAction(LoginUiAction.OnForgotPasswordClicked) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onGoogleSignInClicked: (Context) -> Unit,
    onRegisterClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            STTopAppBar(titleRes = R.string.login)
        },
        modifier = modifier
    ) { innerPadding ->
        val contentPadding = Modifier.padding(innerPadding)
        LoginContent(
            uiState = uiState,
            onEmailChanged = onEmailChanged,
            onPasswordChanged = onPasswordChanged,
            onLoginClicked = onLoginClicked,
            onGoogleSignInClicked = onGoogleSignInClicked,
            onRegisterClicked = onRegisterClicked,
            onForgotPasswordClicked = onForgotPasswordClicked,
            modifier = contentPadding
        )
    }
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onGoogleSignInClicked: (Context) -> Unit,
    onRegisterClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogInForm(
            uiState = uiState,
            onEmailChanged = onEmailChanged,
            onPasswordChanged = onPasswordChanged,
            onLoginClicked = onLoginClicked
        )
        TextButton(onClick = onForgotPasswordClicked) {
            Text(
                text = stringResource(id = R.string.forgot_password),
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.error
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(id = R.string.no_account))
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(id = R.string.signup),
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onRegisterClicked() }
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.or),
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            GoogleButton(
                onClick = { onGoogleSignInClicked(context) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun LogInForm(
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        STOutlinedTextField(
            name = uiState.email,
            placeholder = stringResource(id = R.string.email),
            onValueChange = { onEmailChanged(it) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        STPasswordTextField(
            password = uiState.password,
            placeholder = stringResource(id = R.string.password),
            onValueChange = { onPasswordChanged(it) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        SpendTrackButton(
            text = stringResource(id = R.string.login),
            onClick = onLoginClicked
        )
    }
}

@DevicePreviews
@Composable
fun LoginContentPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        SpendTrackBackground {
            LoginContent(
                uiState = LoginUiState(),
                onEmailChanged = {},
                onPasswordChanged = {},
                onLoginClicked = {},
                onGoogleSignInClicked = {},
                onRegisterClicked = {},
                onForgotPasswordClicked = {}
            )
        }
    }
}