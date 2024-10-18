package com.zacle.spendtrack.feature.home

import android.content.Context
import com.zacle.spendtrack.core.domain.HomeUseCase
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.CommonResultConverter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class HomeConverter @Inject constructor(
    @ApplicationContext private val context: Context,
): CommonResultConverter<HomeUseCase.Response, HomeModel>() {
    override fun convertSuccess(data: HomeUseCase.Response): HomeModel =
        HomeModel(
            accountBalance = data.accountBalance,
            amountSpent = data.amountSpent,
            amountEarned = data.amountEarned,
            transactions = data.transactions,
            totalBudget = data.totalBudget,
            remainingBudget = data.remainingBudget,
            budgets = data.budgets,
            transactionsReport = data.transactionsReport
        )

    override fun convertError(useCaseException: UseCaseException): String {
        return context.getString(R.string.unknown_error)
    }

}