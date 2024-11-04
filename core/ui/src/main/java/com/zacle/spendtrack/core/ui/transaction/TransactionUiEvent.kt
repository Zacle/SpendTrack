package com.zacle.spendtrack.core.ui.transaction

import com.zacle.spendtrack.core.ui.UiEvent

sealed class TransactionUiEvent: UiEvent {
    data class BlankNameError(val messageResId: Int): TransactionUiEvent()
    data class InvalidNameError(val messageResId: Int): TransactionUiEvent()
    data class ShortNameError(val messageResId: Int): TransactionUiEvent()
    data class InvalidAmountError(val messageResId: Int): TransactionUiEvent()
    data class CategoryNotSelectedError(val messageResId: Int): TransactionUiEvent()
    data class NavigateToTransactionDetail(val transactionId: String): TransactionUiEvent()
    data object NavigateBack: TransactionUiEvent()
    data object NavigateToLogin: TransactionUiEvent()
}