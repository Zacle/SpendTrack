package com.zacle.spendtrack.core.domain.budget

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

/**
 * Add budget for the current Month. Each budget has a unique category and each category has only one budget.
 *
 * If the budget for the selected category already exists, the amount will be added to the existing budget.
 * Otherwise, a new budget will be created.
 */
class AddBudgetUseCase(
    configuration: Configuration,
    private val budgetRepository: BudgetRepository
): UseCase<AddBudgetUseCase.Request, AddBudgetUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> = flow {
        val budget = request.budget
        var budgetAmount = budget.amount
        var remainingAmount = budgetAmount
        val insertedBudget: Budget

        val budgets = budgetRepository.getBudgets(request.userId, request.period).first()

        /* Check if the selected category has already a budget exists */
        val currentBudget = budgets.find { it.category.categoryId == budget.category.categoryId }

        /* If the budget exists, update it, otherwise create a new one */
        if (currentBudget != null) {
            budgetAmount += currentBudget.amount
            remainingAmount  += currentBudget.remainingAmount
            insertedBudget = currentBudget.copy(
                amount = budgetAmount,
                remainingAmount = remainingAmount,
                updatedAt = Clock.System.now()
            )
            budgetRepository.updateBudget(insertedBudget)
        } else {
            insertedBudget = budget.copy(userId = request.userId, remainingAmount = remainingAmount)
            budgetRepository.addBudget(insertedBudget)
        }

        emit(Response(insertedBudget))
    }

    data class Request(val userId: String, val budget: Budget, val period: Period): UseCase.Request

    data class Response(val budget: Budget): UseCase.Response
}