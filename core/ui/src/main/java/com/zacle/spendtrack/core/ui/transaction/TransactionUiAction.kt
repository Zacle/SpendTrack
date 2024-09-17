package com.zacle.spendtrack.core.ui.transaction

import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.ui.UiAction
import kotlinx.datetime.Instant

sealed class TransactionUiAction: UiAction {
    data class OnNameChanged(val name: String): TransactionUiAction()
    data class OnDescriptionChanged(val description: String): TransactionUiAction()
    data class OnAmountChanged(val amount: Double): TransactionUiAction()
    data class OnCategorySelected(val category: Category): TransactionUiAction()
    data class OnDateSelected(val date: Instant): TransactionUiAction()
    data class OnAttachmentSelected(val attachment: ImageData?): TransactionUiAction()
    data object OnSaveTransaction: TransactionUiAction()
}