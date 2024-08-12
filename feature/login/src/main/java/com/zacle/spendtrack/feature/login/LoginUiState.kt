package com.zacle.spendtrack.feature.login

import com.zacle.spendtrack.core.ui.types.FormError

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: FormError? = null,
    val passwordError: FormError? = null
)