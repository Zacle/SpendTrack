package com.zacle.spendtrack.feature.budget.view_budget

import android.content.Context
import com.zacle.spendtrack.core.domain.budget.GetBudgetDetailsUseCase
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.CommonResultConverter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BudgetDetailConverter @Inject constructor(
    @ApplicationContext private val context: Context
): CommonResultConverter<GetBudgetDetailsUseCase.Response, BudgetDetailModel>() {
    override fun convertSuccess(data: GetBudgetDetailsUseCase.Response): BudgetDetailModel =
        BudgetDetailModel(
            budget = data.budget,
            transactions = data.transactions
        )

    override fun convertError(useCaseException: UseCaseException): String =
        context.getString(R.string.unknown_error)
}