package com.zacle.spendtrack.feature.forgot_password

import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.auth.ForgotPasswordUseCase
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.ext.isValidEmail
import com.zacle.spendtrack.core.ui.types.EmailError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
): BaseViewModel<Unit, UiState<Unit>, ForgotPasswordUiAction, ForgotPasswordUiEvent>() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    override fun initState(): UiState<Unit> = UiState.Loading

    override fun handleAction(action: ForgotPasswordUiAction) {
        when (action) {
            is ForgotPasswordUiAction.OnEmailChanged -> {
                _uiState.value = _uiState.value.copy(email = action.email)
            }
            is ForgotPasswordUiAction.OnSubmitClicked -> {
                onSubmitClicked()
            }
        }
    }

    private fun onSubmitClicked() {
        val email = _uiState.value.email
        verifyEmail(email)
        if (_uiState.value.emailError != null) return

        viewModelScope.launch {
            forgotPasswordUseCase.execute(ForgotPasswordUseCase.Request(email))
                .collectLatest { response ->
                    if (response is Result.Success) {
                        val isMailSent = response.data.success
                        if (isMailSent) {
                            submitSingleEvent(ForgotPasswordUiEvent.NavigateToLogin)
                        } else {
                            submitSingleEvent(ForgotPasswordUiEvent.EmailNotFound)
                        }
                    } else {
                        submitSingleEvent(ForgotPasswordUiEvent.EmailNotFound)
                    }
                }
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