package com.zacle.spendtrack.core.ui.transaction

data class TransactionDetailUiState(
    val userId: String = "",
    val shouldDisplayRemoveTransactionDialog: Boolean = false,
    val isTransactionDeleted: Boolean = false,
    val isTransactionDeletedDialogDisplaying: Boolean = false
)