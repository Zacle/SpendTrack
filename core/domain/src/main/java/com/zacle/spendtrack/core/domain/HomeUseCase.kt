package com.zacle.spendtrack.core.domain

import com.zacle.spendtrack.core.domain.budget.GetBudgetsUseCase
import com.zacle.spendtrack.core.model.util.FilterState
import com.zacle.spendtrack.core.model.util.SortOrder
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import com.zacle.spendtrack.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.YearMonth

class HomeUseCase(
    configuration: Configuration,
    private val overviewUseCase: OverviewUseCase,
    private val transactionsUseCase: TransactionsUseCase,
    private val budgetsUseCase: GetBudgetsUseCase
): UseCase<HomeUseCase.Request, HomeUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        combine(
            overviewUseCase.process(OverviewUseCase.Request(request.userId, request.period)),
            transactionsUseCase.process(TransactionsUseCase.Request(request.userId, request.period, request.filterState, request.sortOrder)),
            budgetsUseCase.process(GetBudgetsUseCase.Request(request.userId, request.period))
        ) { overviewResponse, transactionsResponse, budgetsResponse ->
            Response(
                accountBalance = overviewResponse.accountBalance,
                amountSpent = overviewResponse.amountSpent,
                amountEarned = overviewResponse.amountEarned,
                transactions = transactionsResponse.transactions,
                totalBudget = budgetsResponse.totalBudget,
                remainingBudget = budgetsResponse.remainingBudget,
                budgets = budgetsResponse.budgets,
                transactionsReport = groupTransactionByDay(request.period.start, transactionsResponse.transactions)
            )
        }

    private fun groupTransactionByDay(instant: Instant, transactions: List<Transaction>): Map<Int, Int> {
        val daysInMonth = getLengthOfMonth(instant)
        // Initialize a map with all days of the month and default amounts of 0
        val transactionMap = mutableMapOf<Int, Int>().apply {
            for (day in 1..daysInMonth) {
                this[day] = 0
            }
        }

        // Group transactions by the day of the month and sum their amounts
        transactions.groupBy {
            val locatedDate = it.transactionDate.toLocalDateTime(TimeZone.currentSystemDefault())
            locatedDate.dayOfMonth
        }.forEach { (day, transactionsOnDay) ->
            transactionMap[day] = transactionsOnDay.sumOf { it.amount }.toInt()
        }

        return transactionMap
    }

    private fun getLengthOfMonth(instant: Instant, timeZone: TimeZone = TimeZone.currentSystemDefault()): Int {
        // Convert Instant to LocalDateTime using the given timezone
        val localDateTime = instant.toLocalDateTime(timeZone)

        // Extract the year and month from the LocalDateTime
        val yearMonth = YearMonth.of(localDateTime.year, localDateTime.monthNumber)

        // Get the number of days in the month
        return yearMonth.lengthOfMonth()
    }

    data class Request(
        val userId: String,
        val period: Period,
        val filterState: FilterState = FilterState(),
        val sortOrder: SortOrder = SortOrder.NEWEST
    ): UseCase.Request

    data class Response(
        val accountBalance: Double,
        val amountSpent: Double,
        val amountEarned: Double,
        val transactions: List<Transaction>,
        val totalBudget: Double,
        val remainingBudget: Double,
        val budgets: List<Budget>,
        val transactionsReport: Map<Int, Int>
    ): UseCase.Response
}