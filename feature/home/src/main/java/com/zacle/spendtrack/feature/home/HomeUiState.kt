package com.zacle.spendtrack.feature.home

import com.zacle.spendtrack.core.model.Period
import com.zacle.spendtrack.core.model.User
import com.zacle.spendtrack.core.model.util.period.toMonthlyPeriod
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Represents the UI state for the Home screen.
 *
 * @param selectedDate The date currently selected by the user, displayed at the top of the screen. This date
 * is used as the reference for fetching transactions and categories.
 * @param transactionPeriod The time period for which transactions (expenses and incomes) are retrieved and displayed on the home screen.
 * @param isTransactionViewActive Determines whether the screen is displaying a list of transactions or categories.
 * @param appliedFilterPeriod The period actively used to filter transactions and categories, which may differ from [transactionPeriod].
 */
data class HomeUiState(
    val selectedDate: Instant = Clock.System.now(),
    val transactionPeriod: Period = selectedDate.toMonthlyPeriod(),
    val transactionPeriodType: TransactionPeriodType = TransactionPeriodType.MONTHLY,
    val isTransactionViewActive: Boolean = true,
    val appliedFilterPeriod: Period = selectedDate.toMonthlyPeriod(),
    val user: User? = null
)

enum class TransactionPeriodType {
    DAILY, WEEKLY, MONTHLY, YEARLY
}
