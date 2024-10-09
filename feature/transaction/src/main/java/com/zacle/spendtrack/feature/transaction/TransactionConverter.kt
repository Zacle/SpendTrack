package com.zacle.spendtrack.feature.transaction

import android.content.Context
import com.zacle.spendtrack.core.domain.TransactionsUseCase
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.CommonResultConverter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TransactionConverter @Inject constructor(
    @ApplicationContext private val context: Context
): CommonResultConverter<TransactionsUseCase.Response, TransactionModel>() {
    override fun convertSuccess(data: TransactionsUseCase.Response): TransactionModel =
        TransactionModel(
            transactions = data.transactions
        )

    override fun convertError(useCaseException: UseCaseException): String =
        context.getString(R.string.unknown_error)
}