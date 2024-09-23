package com.zacle.spendtrack.core.ui.transaction

import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.ImageData
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class TransactionUiState(
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val amount: Int = 0,
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category = Category(),
    val transactionDate: Instant = Clock.System.now(),
    val receiptImage: ImageData? = null,
    val isLoading: Boolean = false,
    val nameError: TransactionFormError? = null,
    val amountError: TransactionFormError? = null,
    val categoryError: TransactionFormError? = null,
)
