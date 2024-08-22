package com.zacle.spendtrack.core.domain.budget

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Exceptions.BudgetNotFoundException
import com.zacle.spendtrack.core.model.Period
import com.zacle.spendtrack.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Get budget details for a specific budget category. We also need to get the transactions (incomes and expenses)
 * for the budget category and sort them by the transaction date.
 *
 * @throws BudgetNotFoundException if the budget is not found
 */
class GetBudgetDetailsUseCase(
    configuration: Configuration,
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository
): UseCase<GetBudgetDetailsUseCase.Request, GetBudgetDetailsUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        combine(
            budgetRepository.getBudget(request.userId, request.budgetId, request.budgetPeriod),
            expenseRepository.getExpensesByCategory(request.userId, request.categoryId, request.budgetPeriod),
            incomeRepository.getIncomesByCategory(request.userId, request.categoryId, request.budgetPeriod)
        ) { budget, expenses, incomes ->
            val transactions: List<Transaction> = expenses + incomes

            if (budget == null) throw BudgetNotFoundException()

            Response(
                budget,
                transactions.sortedByDescending { it.transactionDate }
            )
        }

    data class Request(val userId: String, val budgetId: String, val categoryId: String, val budgetPeriod: Period): UseCase.Request

    data class Response(val budget: Budget, val transactions: List<Transaction>): UseCase.Response
}