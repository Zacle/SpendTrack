package com.zacle.spendtrack.feature.login

import android.content.Context
import com.zacle.spendtrack.core.ui.UiAction

sealed class LoginUiAction: UiAction {
    data class OnEmailChanged(val email: String): LoginUiAction()
    data class OnPasswordChanged(val password: String): LoginUiAction()
    data object OnLoginClicked: LoginUiAction()
    data object OnRegisterClicked: LoginUiAction()
    data object OnForgotPasswordClicked: LoginUiAction()
    data class OnGoogleSignInClicked(val context: Context): LoginUiAction()
}