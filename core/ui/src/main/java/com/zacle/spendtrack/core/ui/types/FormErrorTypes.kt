package com.zacle.spendtrack.core.ui.types

import com.zacle.spendtrack.core.shared_resources.R as SharedR

interface FormError {
    val errorMessageResId: Int?
}

enum class NameError(override val errorMessageResId: Int): FormError {
    BLANK(SharedR.string.name_error_blank),
    INVALID(SharedR.string.name_error_invalid),
    SHORT(SharedR.string.name_error_short)
}

enum class EmailError(override val errorMessageResId: Int): FormError {
    BLANK(SharedR.string.email_error_blank),
    INVALID(SharedR.string.email_error_invalid)
}

enum class PasswordError(override val errorMessageResId: Int): FormError {
    BLANK(SharedR.string.password_error_blank),
    SHORT(SharedR.string.password_error_short),
    INVALID(SharedR.string.password_error_invalid)
}