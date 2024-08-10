package com.zacle.spendtrack.feature.forgot_password

import com.zacle.spendtrack.core.ui.UiEvent

sealed class ForgotPasswordUiEvent: UiEvent {
    data object EmailNotFound: ForgotPasswordUiEvent()
    data object NavigateToLogin: ForgotPasswordUiEvent()
}