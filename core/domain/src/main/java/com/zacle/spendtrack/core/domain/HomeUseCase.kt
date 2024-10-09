package com.zacle.spendtrack.core.domain

import com.zacle.spendtrack.core.domain.budget.GetBudgetsUseCase
import com.zacle.spendtrack.core.model.util.FilterState
import com.zacle.spendtrack.core.model.util.SortOrder
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import com.zacle.spendtrack.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class HomeUseCase(
    configuration: Configuration,
    private val overviewUseCase: OverviewUseCase,
    private val transactionsUseCase: TransactionsUseCase,
    private val budgetsUseCase: GetBudgetsUseCase
): UseCase<HomeUseCase.Request, HomeUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        combine(
            overviewUseCase.process(OverviewUseCase.Request(request.userId, request.period)),
            transactionsUseCase.process(TransactionsUseCase.Request(request.userId, request.appliedFilterPeriod, request.filterState, request.sortOrder)),
            budgetsUseCase.process(GetBudgetsUseCase.Request(request.userId, request.appliedFilterPeriod))
        ) { overviewResponse, transactionsResponse, budgetsResponse ->
            Response(
                accountBalance = overviewResponse.accountBalance,
                amountSpent = overviewResponse.amountSpent,
                amountEarned = overviewResponse.amountEarned,
                transactions = transactionsResponse.transactions,
                totalBudget = budgetsResponse.totalBudget,
                remainingBudget = budgetsResponse.remainingBudget,
                budgets = budgetsResponse.budgets
            )
        }

    data class Request(
        val userId: String,
        val period: Period,
        val appliedFilterPeriod: Period,
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
        val budgets: List<Budget>
    ): UseCase.Response
}