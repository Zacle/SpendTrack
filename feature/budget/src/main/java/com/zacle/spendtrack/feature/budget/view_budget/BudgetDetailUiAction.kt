package com.zacle.spendtrack.feature.budget.view_budget

import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.ui.UiAction

sealed class BudgetDetailUiAction: UiAction {
    data class OnEditPressed(val budgetId: String): BudgetDetailUiAction()
    data object OnDeletePressed: BudgetDetailUiAction()
    data object OnDeleteDismissed: BudgetDetailUiAction()
    data class OnDeleteConfirmed(val budget: Budget): BudgetDetailUiAction()
    data class OnExpenseClicked(val expenseId: String): BudgetDetailUiAction()
    data class OnIncomeClicked(val incomeId: String): BudgetDetailUiAction()
}