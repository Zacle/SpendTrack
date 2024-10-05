package com.zacle.spendtrack.feature.budget.budgets

import com.zacle.spendtrack.core.ui.UiAction

sealed class BudgetsUiAction: UiAction {
    data object Load: BudgetsUiAction()
    data object OnNextMonthPressed: BudgetsUiAction()
    data object OnPreviousMonthPressed: BudgetsUiAction()
}