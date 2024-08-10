package com.zacle.spendtrack.feature.register

import android.content.Context
import com.zacle.spendtrack.core.ui.UiAction

sealed class RegisterUiAction: UiAction {
    data class OnFirstNameChanged(val firstName: String): RegisterUiAction()
    data class OnLastNameChanged(val lastName: String): RegisterUiAction()
    data class OnEmailChanged(val email: String): RegisterUiAction()
    data class OnPasswordChanged(val password: String): RegisterUiAction()
    data class OnGoogleSignInClicked(val context: Context): RegisterUiAction()
    data object OnRegisterClicked: RegisterUiAction()
    data object OnLoginClicked: RegisterUiAction()
}