package com.zacle.spendtrack.feature.budget.add_edit_budget

import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.ui.UiAction

sealed class AddEditBudgetUiAction: UiAction {
    data class OnAmountChanged(val amount: Double): AddEditBudgetUiAction()
    data class OnCategorySelected(val category: Category): AddEditBudgetUiAction()
    data class OnBudgetAlertChanged(val budgetAlert: Boolean): AddEditBudgetUiAction()
    data class OnBudgetAlertPercentageChanged(val budgetAlertPercentage: Int): AddEditBudgetUiAction()
    data class OnRecurrentChanged(val recurrent: Boolean): AddEditBudgetUiAction()
    data object OnSaveBudget: AddEditBudgetUiAction()
}