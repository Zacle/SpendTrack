package com.zacle.spendtrack.core.domain.expense

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Exceptions.BudgetNotFoundException
import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

/**
 * Delete the expense. Add the expense amount to the corresponding category budget remaining amount.
 */
class DeleteExpenseUseCase(
    configuration: Configuration,
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository
): UseCase<DeleteExpenseUseCase.Request, DeleteExpenseUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> {
        val expense = request.expense

        val budgets = budgetRepository.getBudgets(request.userId, request.period).first()
        val categoryBudget: Budget = budgets
            .find { it.category.categoryId == expense.category.categoryId }
            ?: throw BudgetNotFoundException()

        val remainingAmount = categoryBudget.remainingAmount + expense.amount
        if (remainingAmount <= categoryBudget.amount) {
            budgetRepository.updateBudget(categoryBudget.copy(remainingAmount = remainingAmount))
        }

        expenseRepository.deleteExpense(expense)
        return flowOf(Response)
    }

    data class Request(val userId: String, val expense: Expense, val period: Period): UseCase.Request

    data object Response: UseCase.Response
}