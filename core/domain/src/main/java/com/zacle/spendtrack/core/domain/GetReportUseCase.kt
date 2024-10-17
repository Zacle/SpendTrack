package com.zacle.spendtrack.core.domain

import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.Period
import com.zacle.spendtrack.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.YearMonth

class GetReportUseCase(
    configuration: Configuration,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository
): UseCase<GetReportUseCase.Request, GetReportUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        combine(
            expenseRepository.getExpenses(request.userId, request.period),
            incomeRepository.getIncomes(request.userId, request.period)
        ) { expenses, incomes ->
            val expensesReport = groupTransactionByDay(request.period.start, expenses)
            val incomesReport = groupTransactionByDay(request.period.start, incomes)

            val categoryExpensesReport = groupTransactionByCategory(expenses)
            val categoryIncomesReport = groupTransactionByCategory(incomes)

            Response(
                transactions = expenses + incomes,
                expensesReport = expensesReport,
                incomesReport = incomesReport,
                categoryExpensesReport = categoryExpensesReport,
                categoryIncomesReport = categoryIncomesReport
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

    private fun groupTransactionByCategory(transactions: List<Transaction>): Map<Category, CategoryStats> {
        val totalAmount = transactions.sumOf { it.amount }.toInt()
        return transactions
            .groupBy { it.category }
            .mapValues { entry ->
                val totalCategoryAmount = entry.value.sumOf { it.amount }.toInt()
                val categoryPercentage = (totalCategoryAmount.toFloat() / totalAmount.toFloat())
                CategoryStats(totalCategoryAmount, categoryPercentage)
            }
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
        val period: Period
    ): UseCase.Request

    data class Response(
        val transactions: List<Transaction>,
        val expensesReport: Map<Int, Int>,
        val incomesReport: Map<Int, Int>,
        val categoryExpensesReport: Map<Category, CategoryStats>,
        val categoryIncomesReport: Map<Category, CategoryStats>
    ): UseCase.Response
}

data class CategoryStats(
    val categoryAmount: Int,
    val categoryPercentage: Float
)