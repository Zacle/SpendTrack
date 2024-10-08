package com.zacle.spendtrack.core.domain.income

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.model.Exceptions
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock

class UpdateIncomeUseCase(
    configuration: Configuration,
    private val incomeRepository: IncomeRepository,
    private val budgetRepository: BudgetRepository
): UseCase<UpdateIncomeUseCase.Request, UpdateIncomeUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> {
        val income = request.income
        val incomeId = income.id

        val currentIncome = incomeRepository
            .getIncome(request.userId, incomeId)
            .first()
            ?: throw Exceptions.IncomeNotFoundException()

        val incomeAmountDelta = currentIncome.amount - income.amount

        val budgets = budgetRepository.getBudgets(request.userId, request.period).first()
        val categoryBudget = budgets
            .find { it.category.categoryId == income.category.categoryId }

        if (categoryBudget != null) {
            val amount = categoryBudget.amount - incomeAmountDelta
            val remainingAmount = categoryBudget.remainingAmount - incomeAmountDelta
            val updatedBudget = categoryBudget.copy(amount = amount, remainingAmount = remainingAmount, updatedAt = Clock.System.now())
            budgetRepository.updateBudget(updatedBudget)
        }

        incomeRepository.updateIncome(request.income)
        return flowOf(Response)
    }

    data class Request(val userId: String, val income: Income, val period: Period): UseCase.Request

    data object Response: UseCase.Response
}