package com.zacle.spendtrack.core.ui.transaction

import com.zacle.spendtrack.core.ui.UiEvent

sealed class TransactionDetailUiEvent: UiEvent {
    data class NavigateToEditTransaction(val transactionId: String): TransactionDetailUiEvent()
    data object NavigateToLogin: TransactionDetailUiEvent()
}