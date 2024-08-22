package com.zacle.spendtrack.core.domain.expense

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Exceptions
import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class UpdateExpenseUseCase(
    configuration: Configuration,
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository
): UseCase<UpdateExpenseUseCase.Request, UpdateExpenseUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> = flow {
        val newExpense = request.expense
        val expenseId = newExpense.expenseId

        val currentExpense = expenseRepository
            .getExpense(request.userId, expenseId)
            .first()
            ?: throw Exceptions.ExpenseNotFoundException()

        val expenseAmountDelta = currentExpense.amount - newExpense.amount

        val budgets = budgetRepository.getBudgets(request.userId, request.period).first()
        val categoryBudget: Budget = budgets
            .find { it.category.categoryId == newExpense.category.categoryId }
            ?: throw Exceptions.BudgetNotFoundException()


        val remainingAmount = categoryBudget.remainingAmount + expenseAmountDelta
        budgetRepository.updateBudget(request.userId, categoryBudget.copy(remainingAmount = remainingAmount))

        expenseRepository.updateExpense(request.userId, request.expense)
        emit(Response)
    }

    data class Request(val userId: String, val expense: Expense, val period: Period): UseCase.Request

    data object Response: UseCase.Response
}