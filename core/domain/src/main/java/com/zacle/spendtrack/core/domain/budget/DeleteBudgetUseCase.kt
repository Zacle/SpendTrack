package com.zacle.spendtrack.core.domain.budget

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.model.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DeleteBudgetUseCase(
    configuration: Configuration,
    private val budgetRepository: BudgetRepository
): UseCase<DeleteBudgetUseCase.Request, DeleteBudgetUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> {
        budgetRepository.deleteBudget(request.budget)
        return flowOf(Response)
    }

    data class Request(val userId: String, val budget: Budget) : UseCase.Request

    data object Response : UseCase.Response
}