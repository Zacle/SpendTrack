package com.zacle.spendtrack.feature.login

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.auth.AuthenticateWithGoogleUseCase
import com.zacle.spendtrack.core.domain.auth.SignInWithEmailAndPasswordUseCase
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.ext.isValidEmail
import com.zacle.spendtrack.core.ui.types.EmailError
import com.zacle.spendtrack.core.ui.types.PasswordError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    private val authenticateWithGoogleUseCase: AuthenticateWithGoogleUseCase,
): BaseViewModel<Unit, UiState<Unit>, LoginUiAction, LoginUiEvent>() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    override fun initState(): UiState<Unit> = UiState.Loading

    override fun handleAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.OnEmailChanged -> {
                _uiState.value = _uiState.value.copy(email = action.email)
            }
            is LoginUiAction.OnPasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = action.password)
            }
            is LoginUiAction.OnLoginClicked -> {
                onLoginClicked()
            }
            is LoginUiAction.OnGoogleSignInClicked -> {
                onGoogleSignInClicked(action.context)
            }
            is LoginUiAction.OnRegisterClicked -> {
                submitSingleEvent(LoginUiEvent.NavigateToRegister)
            }
            is LoginUiAction.OnForgotPasswordClicked -> {
                submitSingleEvent(LoginUiEvent.NavigateToForgotPassword)
            }
        }
    }

    private fun onLoginClicked() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        formValidation(email, password)

        val errors = listOf(
            _uiState.value.emailError,
            _uiState.value.passwordError
        )
        if (errors.any { it != null }) return

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            signInWithEmailAndPasswordUseCase.execute(
                SignInWithEmailAndPasswordUseCase.Request(email, password)
            )
                .collectLatest { response ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    if (response is Result.Success) {
                        val result = response.data.authResult
                        /* If the user is not verified, navigate to the verify email screen. */
                        if (result.isEmailVerified == false) {
                            submitSingleEvent(LoginUiEvent.NavigateToVerifyEmail)
                        } else {
                            submitSingleEvent(LoginUiEvent.NavigateToHome)
                        }
                        Timber.d("Response = $result")
                    } else {
                        submitSingleEvent(LoginUiEvent.LoginFailed)
                    }
                }
        }
    }

    private fun onGoogleSignInClicked(context: Context) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            authenticateWithGoogleUseCase.execute(
                AuthenticateWithGoogleUseCase.Request(context)
            ).collectLatest { response ->
                _uiState.value = _uiState.value.copy(isLoading = true)
                if (response is Result.Success) {
                    submitSingleEvent(LoginUiEvent.NavigateToHome)
                } else {
                    submitSingleEvent(LoginUiEvent.LoginFailed)
                }
            }
        }
    }

    private fun formValidation(email: String, password: String) {
        verifyEmail(email)
        verifyPassword(password)
    }

    private fun verifyEmail(email: String) {
        if (!email.isValidEmail()) {
            _uiState.value = uiState.value.copy(emailError = EmailError.INVALID)
        } else {
            _uiState.value = uiState.value.copy(emailError = null)
        }

        if (email.isBlank()) {
            _uiState.value = uiState.value.copy(emailError = EmailError.BLANK)
        } else {
            _uiState.value = uiState.value.copy(emailError = null)
        }
    }

    private fun verifyPassword(password: String) {
        if (password.isBlank()) {
            _uiState.value = uiState.value.copy(passwordError = PasswordError.BLANK)
        } else {
            _uiState.value = uiState.value.copy(passwordError = null)
        }
    }
}