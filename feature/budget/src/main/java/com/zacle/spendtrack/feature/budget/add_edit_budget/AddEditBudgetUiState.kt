package com.zacle.spendtrack.feature.budget.add_edit_budget

import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.ui.transaction.TransactionFormError

data class AddEditBudgetUiState(
    val userId: String = "",
    val budgetId: String? = null,
    val amount: Int = 0,
    val currencyCode: String = "USD",
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category = Category(),
    val recurrent: Boolean = false,
    val budgetAlert: Boolean = false,
    val budgetAlertPercentage: Int = 100,
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val amountError: TransactionFormError? = null,
    val categoryError: TransactionFormError? = null,
)
