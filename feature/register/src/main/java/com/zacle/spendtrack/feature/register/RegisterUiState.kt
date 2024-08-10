package com.zacle.spendtrack.feature.register

import com.zacle.spendtrack.core.ui.types.FormError

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val firstNameError: FormError? = null,
    val lastNameError: FormError? = null,
    val emailError: FormError? = null,
    val passwordError: FormError? = null
)
