package com.zacle.spendtrack.core.domain.budget

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteBudgetUseCase(
    configuration: Configuration,
    private val budgetRepository: BudgetRepository
): UseCase<DeleteBudgetUseCase.Request, DeleteBudgetUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> = flow {
        budgetRepository.deleteBudget(request.userId, request.budgetId)
        emit(Response)
    }

    data class Request(val userId: String, val budgetId: String) : UseCase.Request

    data object Response : UseCase.Response
}