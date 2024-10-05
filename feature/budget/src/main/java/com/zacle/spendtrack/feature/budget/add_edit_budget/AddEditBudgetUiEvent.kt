package com.zacle.spendtrack.feature.budget.add_edit_budget

import com.zacle.spendtrack.core.ui.UiEvent

sealed class AddEditBudgetUiEvent: UiEvent {
    data class InvalidAmountError(val messageResId: Int): AddEditBudgetUiEvent()
    data class CategoryNotSelectedError(val messageResId: Int): AddEditBudgetUiEvent()
    data object NavigateBack: AddEditBudgetUiEvent()
    data object NavigateToLogin: AddEditBudgetUiEvent()
}