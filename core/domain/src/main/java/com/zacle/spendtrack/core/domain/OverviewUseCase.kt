package com.zacle.spendtrack.core.domain

import com.zacle.spendtrack.core.domain.budget.GetBudgetsUseCase
import com.zacle.spendtrack.core.domain.expense.GetExpensesUseCase
import com.zacle.spendtrack.core.domain.income.GetIncomesUseCase
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class OverviewUseCase(
    configuration: Configuration,
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getIncomesUseCase: GetIncomesUseCase
): UseCase<OverviewUseCase.Request, OverviewUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        combine(
            getBudgetsUseCase.process(GetBudgetsUseCase.Request(request.userId, request.period)),
            getExpensesUseCase.process(GetExpensesUseCase.Request(request.userId, emptySet(), request.period)),
            getIncomesUseCase.process(GetIncomesUseCase.Request(request.userId, emptySet(), request.period))
        ) { budgets, expenses, incomes ->
            Response(
                budgets.remainingBudget,
                expenses.amountSpent,
                incomes.amountEarned
            )
        }

    data class Request(val userId: String, val period: Period) : UseCase.Request

    data class Response(val accountBalance: Double, val amountSpent: Double, val amountEarned: Double) : UseCase.Response
}