package com.zacle.spendtrack.core.ui.types

import com.zacle.spendtrack.core.ui.R

interface FormError {
    val errorMessageResId: Int?
}

enum class NameError(override val errorMessageResId: Int): FormError {
    BLANK(R.string.name_error_blank),
    INVALID(R.string.name_error_invalid),
    SHORT(R.string.name_error_short)
}

enum class EmailError(override val errorMessageResId: Int): FormError {
    BLANK(R.string.email_error_blank),
    INVALID(R.string.email_error_invalid)
}

enum class PasswordError(override val errorMessageResId: Int): FormError {
    BLANK(R.string.password_error_blank),
    SHORT(R.string.password_error_short),
    INVALID(R.string.password_error_invalid)
}