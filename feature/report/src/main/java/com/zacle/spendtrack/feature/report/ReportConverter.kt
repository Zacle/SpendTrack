package com.zacle.spendtrack.feature.report

import android.content.Context
import com.zacle.spendtrack.core.domain.GetReportUseCase
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.CommonResultConverter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReportConverter @Inject constructor(
    @ApplicationContext private val context: Context
): CommonResultConverter<GetReportUseCase.Response, ReportModel>() {
    override fun convertSuccess(data: GetReportUseCase.Response): ReportModel =
        ReportModel(
            totalMonthlyExpenses = data.expensesReport.values.sum(),
            totalMonthlyIncomes = data.incomesReport.values.sum(),
            expensesReport = data.expensesReport,
            incomesReport = data.incomesReport,
            categoryExpensesReport = data.categoryExpensesReport,
            categoryIncomesReport = data.categoryIncomesReport,
            transactions = data.transactions
        )


    override fun convertError(useCaseException: UseCaseException): String =
        context.getString(R.string.unknown_error)
}