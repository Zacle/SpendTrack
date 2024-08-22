package com.zacle.spendtrack.core.domain.expense

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetExpenseUseCase(
    configuration: Configuration,
    private val expenseRepository: ExpenseRepository
): UseCase<GetExpenseUseCase.Request, GetExpenseUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response>  =
        expenseRepository.getExpense(request.userId, request.expenseId).map { expense ->
            Response(expense)
        }

    data class Request(val userId: String, val expenseId: String): UseCase.Request

    data class Response(val expense: Expense?): UseCase.Response
}