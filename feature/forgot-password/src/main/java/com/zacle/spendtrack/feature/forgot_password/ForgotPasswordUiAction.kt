package com.zacle.spendtrack.feature.forgot_password

import com.zacle.spendtrack.core.ui.UiAction

sealed class ForgotPasswordUiAction: UiAction {
    data class OnEmailChanged(val email: String): ForgotPasswordUiAction()
    data object OnSubmitClicked: ForgotPasswordUiAction()
}