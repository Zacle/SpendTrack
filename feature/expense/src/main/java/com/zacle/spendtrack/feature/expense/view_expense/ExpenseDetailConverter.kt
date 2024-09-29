package com.zacle.spendtrack.feature.expense.view_expense

import android.content.Context
import com.zacle.spendtrack.core.domain.expense.GetExpenseUseCase
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import com.zacle.spendtrack.core.ui.CommonResultConverter
import com.zacle.spendtrack.core.ui.transaction.TransactionModel
import com.zacle.spendtrack.core.shared_resources.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExpenseDetailConverter @Inject constructor(
    @ApplicationContext private val context: Context
): CommonResultConverter<GetExpenseUseCase.Response, TransactionModel>() {
    override fun convertSuccess(data: GetExpenseUseCase.Response): TransactionModel =
        TransactionModel(transaction = data.expense)

    override fun convertError(useCaseException: UseCaseException): String =
        context.getString(R.string.unknown_error)
}