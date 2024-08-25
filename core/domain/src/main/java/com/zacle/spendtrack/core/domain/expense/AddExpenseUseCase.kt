package com.zacle.spendtrack.core.domain.expense

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Exceptions.CategoryBudgetNotExistsException
import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * Add expense to the database. We also need to update the corresponding category budget remaining amount.
 *
 * @throws CategoryBudgetNotExistsException if the category budget does not exist
 */
class AddExpenseUseCase(
    configuration: Configuration,
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository
): UseCase<AddExpenseUseCase.Request, AddExpenseUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> = flow {
        val expense = request.expense
        val budgets = budgetRepository.getBudgets(request.userId, request.period).first()
        val categoryBudget: Budget? = budgets.find { it.category.categoryId == expense.category.categoryId }

        if (categoryBudget == null) throw CategoryBudgetNotExistsException()

        val remainingAmount = categoryBudget.remainingAmount - expense.amount
        budgetRepository.updateBudget(categoryBudget.copy(remainingAmount = remainingAmount))

        expenseRepository.addExpense(request.expense.copy(userId = request.userId))
        emit(Response)
    }


    data class Request(val userId: String, val expense: Expense, val period: Period): UseCase.Request

    data object Response: UseCase.Response
}