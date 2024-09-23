package com.zacle.spendtrack.core.ui.transaction

import com.zacle.spendtrack.core.shared_resources.R as SharedR

interface TransactionFormError {
    val errorMessageResId: Int
}

enum class NameError(override val errorMessageResId: Int) : TransactionFormError {
    BLANK(SharedR.string.name_error_blank),
    SHORT(SharedR.string.name_error_short)
}

enum class CategoryError(override val errorMessageResId: Int) : TransactionFormError {
    NOT_SELECTED(SharedR.string.category_not_selected)
}

enum class AmountError(override val errorMessageResId: Int) : TransactionFormError {
    INVALID(SharedR.string.amount_error_invalid)
}