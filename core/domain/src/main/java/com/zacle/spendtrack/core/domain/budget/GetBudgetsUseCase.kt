package com.zacle.spendtrack.core.domain.budget

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetBudgetsUseCase(
    configuration: Configuration,
    private val budgetRepository: BudgetRepository
): UseCase<GetBudgetsUseCase.Request, GetBudgetsUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        budgetRepository.getBudgets(request.userId, request.budgetPeriod).map { budgets ->
            val totalBudget = budgets.sumOf { it.amount }
            val remainingBudget = budgets.sumOf { if (it.remainingAmount > 0) it.remainingAmount else 0.0 }
            Response(totalBudget, remainingBudget, budgets)
        }

    data class Request(val userId: String, val budgetPeriod: Period): UseCase.Request

    data class Response(val totalBudget: Double, val remainingBudget: Double, val budgets: List<Budget>): UseCase.Response
}