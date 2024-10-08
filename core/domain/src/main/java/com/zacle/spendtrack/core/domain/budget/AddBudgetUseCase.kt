package com.zacle.spendtrack.core.domain.budget

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock

/**
 * Add budget for the current Month. Each budget has a unique category and each category has only one budget.
 *
 * If the budget for the selected category already exists, the amount will be added to the existing budget.
 * Otherwise, a new budget will be created.
 */
class AddBudgetUseCase(
    configuration: Configuration,
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository
): UseCase<AddBudgetUseCase.Request, AddBudgetUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> {
        val budget = request.budget
        val budgetAmount = budget.amount
        val insertedBudget: Budget

        val budgets = budgetRepository.getBudgets(request.userId, request.period).first()

        // Retrieve prior expenses for this category and period
        val expenses = expenseRepository.getExpenses(request.userId, request.period).first()
            .filter { it.category.categoryId == budget.category.categoryId }
        // Sum all prior expenses
        val expensesAmount = expenses.sumOf { it.amount }

        // Retrieve prior incomes for this category and period
        val incomes = incomeRepository.getIncomes(request.userId, request.period).first()
            .filter { it.category.categoryId == budget.category.categoryId }
        // Sum all prior incomes
        val incomesAmount = incomes.sumOf { it.amount }

        /* Check if the selected category has already a budget exists */
        val currentBudget = budgets.find { it.category.categoryId == budget.category.categoryId }

        /* If the budget exists, update it, otherwise create a new one */
        if (currentBudget != null) {
            val remainingAmount = budgetAmount - currentBudget.amount + currentBudget.remainingAmount
            insertedBudget = currentBudget.copy(
                amount = budgetAmount,
                remainingAmount = remainingAmount,
                updatedAt = Clock.System.now(),
                recurrent = budget.recurrent,
                budgetAlert = budget.budgetAlert,
                budgetAlertPercentage = budget.budgetAlertPercentage,
                synced = false
            )
            budgetRepository.updateBudget(insertedBudget)
        } else {
            val totalAmount = budgetAmount + incomesAmount
            val remainingAmount = totalAmount - expensesAmount
            insertedBudget = budget.copy(userId = request.userId, amount = totalAmount, remainingAmount = remainingAmount)
            budgetRepository.addBudget(insertedBudget)
        }

        return flowOf(Response(insertedBudget))
    }

    data class Request(val userId: String, val budget: Budget, val period: Period): UseCase.Request

    data class Response(val budget: Budget): UseCase.Response
}