package com.zacle.spendtrack.feature.budget.budgets

import android.content.Context
import com.zacle.spendtrack.core.domain.budget.GetBudgetsUseCase
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.CommonResultConverter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BudgetsConverter @Inject constructor(
    @ApplicationContext private val context: Context
): CommonResultConverter<GetBudgetsUseCase.Response, BudgetsModel>() {
    override fun convertSuccess(data: GetBudgetsUseCase.Response): BudgetsModel =
        BudgetsModel(
            totalBudget = data.totalBudget,
            remainingBudget = data.remainingBudget,
            budgets = data.budgets
        )

    override fun convertError(useCaseException: UseCaseException): String =
        context.getString(R.string.unknown_error)

}