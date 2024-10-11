package com.zacle.spendtrack.core.domain

import com.zacle.spendtrack.core.domain.expense.GetExpensesUseCase
import com.zacle.spendtrack.core.domain.income.GetIncomesUseCase
import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.Period
import com.zacle.spendtrack.core.model.Transaction
import com.zacle.spendtrack.core.model.util.FilterState
import com.zacle.spendtrack.core.model.util.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Get transactions for a specific user and period. We filter the transactions by the category ids
 * and transaction type (income or expense). Then we sort the transactions by the sort order.
 */
class TransactionsUseCase(
    configuration: Configuration,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getIncomesUseCase: GetIncomesUseCase
): UseCase<TransactionsUseCase.Request, TransactionsUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        combine(
            getExpensesUseCase.process(
                GetExpensesUseCase.Request(
                    userId = request.userId,
                    categoryIds = request.filterState.categoryIds,
                    period = request.period
                )
            ),
            getIncomesUseCase.process(
                GetIncomesUseCase.Request(
                    userId = request.userId,
                    categoryIds = request.filterState.categoryIds,
                    period = request.period
                )
            )
        ) { expensesResponse, incomesResponse ->
            val filterState = request.filterState
            val sortOrder = request.sortOrder

            val transactions = expensesResponse.expenses + incomesResponse.incomes

            /**
             * We need to see if we can include the incomes and expenses in the results or filter them out.
             * Then we filter the transactions by the category ids.
             */
            val filteredTransactions = transactions.filter { transaction ->
                (filterState.includeIncomes && transaction is Income) || (filterState.includeExpenses && transaction is Expense)
            }.filter { transaction ->
                filterState.categoryIds.isEmpty() || transaction.category.categoryId in filterState.categoryIds
            }

            /**
             * Sort the transactions by the sort order.
             */
            val sortedTransactions = when (sortOrder) {
                SortOrder.HIGHEST -> filteredTransactions.sortedByDescending { it.amount }
                SortOrder.LOWEST -> filteredTransactions.sortedBy { it.amount }
                SortOrder.NEWEST -> filteredTransactions.sortedByDescending { it.transactionDate }
                SortOrder.OLDEST -> filteredTransactions.sortedBy { it.transactionDate }
            }

            Response(sortedTransactions)
        }

    data class Request(
        val userId: String,
        val period: Period,
        val filterState: FilterState,
        val sortOrder: SortOrder
    ): UseCase.Request

    data class Response(val transactions: List<Transaction>): UseCase.Response
}