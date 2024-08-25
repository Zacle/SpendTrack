package com.zacle.spendtrack.core.domain.income

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.model.Exceptions.CategoryBudgetNotExistsException
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * Add income to the database. We also need to update the corresponding category budget amount and remaining amount.
 *
 * @throws CategoryBudgetNotExistsException if the category budget does not exist
 */
class AddIncomeUseCase(
    configuration: Configuration,
    private val incomeRepository: IncomeRepository,
    private val budgetRepository: BudgetRepository
): UseCase<AddIncomeUseCase.Request, AddIncomeUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> = flow {
        val income = request.income
        val budgets = budgetRepository.getBudgets(request.userId, request.period).first()
        val categoryBudget = budgets
            .find { it.category.categoryId == income.category.categoryId }
            ?: throw CategoryBudgetNotExistsException()

        val amount = categoryBudget.amount + income.amount
        val remainingAmount = categoryBudget.remainingAmount + income.amount
        budgetRepository.updateBudget(categoryBudget.copy(amount = amount, remainingAmount = remainingAmount))

        incomeRepository.addIncome(request.income.copy(userId = request.userId))
        emit(Response)
    }

    data class Request(val userId: String, val income: Income, val period: Period): UseCase.Request

    data object Response: UseCase.Response
}