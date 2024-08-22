package com.zacle.spendtrack.core.domain.income

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetIncomesUseCase(
    configuration: Configuration,
    private val incomeRepository: IncomeRepository
): UseCase<GetIncomesUseCase.Request, GetIncomesUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> {
        val (userId, categoryIds, period) = request
        return incomeRepository.getIncomes(userId, period).map {
            val incomes = it.filter { income ->
                categoryIds.isEmpty() || income.category.categoryId in categoryIds
            }
            val amountEarned = incomes.sumOf { income -> income.amount }
            Response(amountEarned, incomes)
        }
    }

    data class Request(
        val userId: String,
        val categoryIds: Set<String> = emptySet(),
        val period: Period
    ): UseCase.Request

    data class Response(val amountEarned: Double, val incomes: List<Income>): UseCase.Response
}