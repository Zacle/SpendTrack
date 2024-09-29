package com.zacle.spendtrack.core.ui.transaction

import com.zacle.spendtrack.core.model.Transaction
import com.zacle.spendtrack.core.ui.UiAction

sealed class TransactionDetailUiAction: UiAction {
    data object OnDeletePressed: TransactionDetailUiAction()
    data object OnDeleteDismissed: TransactionDetailUiAction()
    data class OnDeleteConfirmed(val transaction: Transaction): TransactionDetailUiAction()
    data object OnDismissTransactionDeletedDialog: TransactionDetailUiAction()
    data object OnEditPressed: TransactionDetailUiAction()
}