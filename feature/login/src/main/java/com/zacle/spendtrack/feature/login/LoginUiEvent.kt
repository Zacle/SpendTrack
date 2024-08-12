package com.zacle.spendtrack.feature.login

import com.zacle.spendtrack.core.ui.UiEvent

sealed class LoginUiEvent: UiEvent {
    data object LoginFailed: LoginUiEvent()
    data object NavigateToVerifyEmail: LoginUiEvent()
    data object NavigateToRegister: LoginUiEvent()
    data object NavigateToForgotPassword: LoginUiEvent()
    data object NavigateToHome: LoginUiEvent()
}