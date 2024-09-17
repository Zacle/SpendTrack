package com.zacle.spendtrack.core.ui.transaction

import com.zacle.spendtrack.core.ui.R

interface TransactionFormError {
    val errorMessageResId: Int
}

enum class NameError(override val errorMessageResId: Int) : TransactionFormError {
    BLANK(R.string.name_error_blank),
    INVALID(R.string.name_error_invalid),
    SHORT(R.string.name_error_short)
}

enum class CategoryError(override val errorMessageResId: Int) : TransactionFormError {
    NOT_SELECTED(R.string.category_not_selected)
}

enum class AmountError(override val errorMessageResId: Int) : TransactionFormError {
    INVALID(R.string.amount_error_invalid)
}