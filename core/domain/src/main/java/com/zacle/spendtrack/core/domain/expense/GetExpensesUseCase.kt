package com.zacle.spendtrack.core.domain.expense

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetExpensesUseCase(
    configuration: Configuration,
    private val expenseRepository: ExpenseRepository
): UseCase<GetExpensesUseCase.Request, GetExpensesUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> {
        val (userId, categoryIds, period) = request
        return expenseRepository.getExpenses(userId, period).map {
            val expenses = it.filter { expense ->
                categoryIds.isEmpty() || expense.category.categoryId in categoryIds
            }
            val amountSpent = expenses.sumOf { expense -> expense.amount }
            Response(amountSpent, expenses)
        }
    }

    data class Request(
        val userId: String,
        val categoryIds: Set<String> = emptySet(),
        val period: Period
    ): UseCase.Request

    data class Response(val amountSpent: Double, val expenses: List<Expense>): UseCase.Response
}