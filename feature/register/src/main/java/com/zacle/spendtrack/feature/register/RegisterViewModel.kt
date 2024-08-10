package com.zacle.spendtrack.feature.register

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.auth.AuthenticateWithGoogleUseCase
import com.zacle.spendtrack.core.domain.auth.SignUpWithEmailAndPasswordUseCase
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.ext.isNameLengthValid
import com.zacle.spendtrack.core.ui.ext.isPasswordLengthValid
import com.zacle.spendtrack.core.ui.ext.isValidEmail
import com.zacle.spendtrack.core.ui.ext.isValidName
import com.zacle.spendtrack.core.ui.ext.isValidPassword
import com.zacle.spendtrack.core.ui.types.EmailError
import com.zacle.spendtrack.core.ui.types.NameError
import com.zacle.spendtrack.core.ui.types.PasswordError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpWithEmailAndPasswordUseCase: SignUpWithEmailAndPasswordUseCase,
    private val authenticateWithGoogleUseCase: AuthenticateWithGoogleUseCase
): BaseViewModel<Unit, UiState<Unit>, RegisterUiAction, RegisterUiEvent>() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    override fun initState(): UiState<Unit> = UiState.Loading

    override fun handleAction(action: RegisterUiAction) {
        when (action) {
            is RegisterUiAction.OnFirstNameChanged -> {
                _uiState.value = _uiState.value.copy(firstName = action.firstName)
            }
            is RegisterUiAction.OnLastNameChanged -> {
                _uiState.value = _uiState.value.copy(lastName = action.lastName)
            }
            is RegisterUiAction.OnEmailChanged -> {
                _uiState.value = _uiState.value.copy(email = action.email)
            }
            is RegisterUiAction.OnPasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = action.password)
            }
            is RegisterUiAction.OnRegisterClicked -> {
                onRegisterClicked()
            }
            is RegisterUiAction.OnGoogleSignInClicked -> {
                onGoogleSignInClicked(action.context)
            }
            is RegisterUiAction.OnLoginClicked -> {
                submitSingleEvent(RegisterUiEvent.NavigateToLogin)
            }
        }
    }

    private fun onGoogleSignInClicked(context: Context) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            authenticateWithGoogleUseCase.execute(
                AuthenticateWithGoogleUseCase.Request(context)
            ).collectLatest { response ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                if (response is Result.Success) {
                    submitSingleEvent(RegisterUiEvent.NavigateToHome)
                } else {
                    submitSingleEvent(RegisterUiEvent.RegistrationFailed)
                }
            }
        }
    }

    private fun onRegisterClicked() {
        val firstName = _uiState.value.firstName
        val lastName = _uiState.value.lastName
        val email = _uiState.value.email
        val password = _uiState.value.password

        formValidation(firstName, lastName, email, password)

        val errors = listOf(
            _uiState.value.firstNameError,
            _uiState.value.lastNameError,
            _uiState.value.emailError,
            _uiState.value.passwordError
        )
        if (errors.any { it != null }) return

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            signUpWithEmailAndPasswordUseCase.execute(
                SignUpWithEmailAndPasswordUseCase.Request(firstName, lastName, email, password)
            )
                .collectLatest { response ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    if (response is Result.Success) {
                        submitSingleEvent(RegisterUiEvent.NavigateToVerifyEmail)
                    } else {
                        submitSingleEvent(RegisterUiEvent.RegistrationFailed)
                    }
                }
        }
    }

    private fun formValidation(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        verifyFirstName(firstName)
        verifyLastName(lastName)
        verifyEmail(email)
        verifyPassword(password)
    }

    private fun verifyFirstName(firstName: String) {
        if (firstName.isBlank()) {
            _uiState.value = uiState.value.copy(firstNameError = NameError.BLANK)
        } else {
            _uiState.value = uiState.value.copy(firstNameError = null)
        }

        if (!firstName.isValidName()) {
            _uiState.value = uiState.value.copy(firstNameError = NameError.INVALID)
        } else {
            _uiState.value = uiState.value.copy(firstNameError = null)
        }

        if (!firstName.isNameLengthValid()) {
            _uiState.value = uiState.value.copy(firstNameError = NameError.SHORT)
        } else {
            _uiState.value = uiState.value.copy(firstNameError = null)
        }
    }

    private fun verifyLastName(lastName: String) {
        if (lastName.isBlank()) {
            _uiState.value = uiState.value.copy(lastNameError = NameError.BLANK)
        } else {
            _uiState.value = uiState.value.copy(lastNameError = null)
        }

        if (!lastName.isNameLengthValid()) {
            _uiState.value = uiState.value.copy(lastNameError = NameError.SHORT)
        } else {
            _uiState.value = uiState.value.copy(lastNameError = null)
        }

        if (!lastName.isValidName()) {
            _uiState.value = uiState.value.copy(lastNameError = NameError.INVALID)
        } else {
            _uiState.value = uiState.value.copy(lastNameError = null)
        }
    }

    private fun verifyPassword(password: String) {
        if (password.isBlank()) {
            _uiState.value = uiState.value.copy(passwordError = PasswordError.BLANK)
        } else {
            _uiState.value = uiState.value.copy(passwordError = null)
        }

        if (!password.isPasswordLengthValid()) {
            _uiState.value = uiState.value.copy(passwordError = PasswordError.SHORT)
        } else {
            _uiState.value = uiState.value.copy(passwordError = null)
        }

        if (!password.isValidPassword()) {
            _uiState.value = uiState.value.copy(passwordError = PasswordError.INVALID)
        } else {
            _uiState.value = uiState.value.copy(passwordError = null)
        }
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
}