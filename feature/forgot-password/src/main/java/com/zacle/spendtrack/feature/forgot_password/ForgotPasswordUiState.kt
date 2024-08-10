package com.zacle.spendtrack.feature.forgot_password

import com.zacle.spendtrack.core.ui.types.FormError

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: FormError? = null
)
