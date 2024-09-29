package com.zacle.spendtrack.feature.income.view_income

import android.content.Context
import com.zacle.spendtrack.core.domain.income.GetIncomeUseCase
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import com.zacle.spendtrack.core.ui.CommonResultConverter
import com.zacle.spendtrack.core.ui.transaction.TransactionModel
import com.zacle.spendtrack.core.shared_resources.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IncomeDetailConverter @Inject constructor(
    @ApplicationContext private val context: Context
): CommonResultConverter<GetIncomeUseCase.Response, TransactionModel>() {
    override fun convertSuccess(data: GetIncomeUseCase.Response): TransactionModel =
        TransactionModel(transaction = data.income)

    override fun convertError(useCaseException: UseCaseException): String =
        context.getString(R.string.unknown_error)
}