package com.zacle.spendtrack.feature.register

import com.zacle.spendtrack.core.ui.UiEvent

sealed class RegisterUiEvent: UiEvent {
    data object RegistrationFailed: RegisterUiEvent()
    data object NavigateToVerifyEmail: RegisterUiEvent()
    data object NavigateToLogin: RegisterUiEvent()
    data object NavigateToHome: RegisterUiEvent()
}