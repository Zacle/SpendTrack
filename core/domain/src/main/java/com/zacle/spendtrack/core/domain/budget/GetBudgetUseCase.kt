package com.zacle.spendtrack.core.domain.budget

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Exceptions.BudgetNotFoundException
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetBudgetUseCase(
    configuration: Configuration,
    private val budgetRepository: BudgetRepository
): UseCase<GetBudgetUseCase.Request, GetBudgetUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        budgetRepository.getBudget(request.userId, request.budgetId, request.budgetPeriod).map {
            if (it == null) throw BudgetNotFoundException()
            Response(it)
        }

    data class Request(val userId: String, val budgetId: String, val budgetPeriod: Period): UseCase.Request

    data class Response(val budget: Budget?): UseCase.Response
}