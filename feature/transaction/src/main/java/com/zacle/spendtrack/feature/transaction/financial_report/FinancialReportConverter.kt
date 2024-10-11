package com.zacle.spendtrack.feature.transaction.financial_report

import android.content.Context
import com.zacle.spendtrack.core.domain.HomeUseCase
import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.CommonResultConverter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FinancialReportConverter @Inject constructor(
    @ApplicationContext private val context: Context
): CommonResultConverter<HomeUseCase.Response, FinancialReportModel>() {
    override fun convertSuccess(data: HomeUseCase.Response): FinancialReportModel {
        val biggestExpense = data.transactions.filterIsInstance<Expense>().maxByOrNull { it.amount }
        val biggestIncome = data.transactions.filterIsInstance<Income>().maxByOrNull { it.amount }
        val exceedingBudgets = data.budgets.filter { it.remainingAmount.toInt() <= 0 }

        return FinancialReportModel(
            amountSpent = data.amountSpent,
            amountEarned = data.amountEarned,
            biggestExpense = biggestExpense,
            biggestIncome = biggestIncome,
            budgetsSize = data.budgets.size,
            exceedingBudgets = exceedingBudgets
        )
    }

    override fun convertError(useCaseException: UseCaseException): String =
        context.getString(R.string.unknown_error)
}