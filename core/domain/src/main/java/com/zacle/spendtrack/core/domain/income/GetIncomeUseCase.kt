package com.zacle.spendtrack.core.domain.income

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.model.Income
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetIncomeUseCase(
    configuration: Configuration,
    private val incomeRepository: IncomeRepository
): UseCase<GetIncomeUseCase.Request, GetIncomeUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        incomeRepository.getIncome(request.userId, request.incomeId).map {
            Response(it)
        }

    data class Request(val userId: String, val incomeId: String): UseCase.Request

    data class Response(val income: Income?): UseCase.Response
}