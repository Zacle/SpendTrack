package com.zacle.spendtrack.core.domain.income

import com.zacle.spendtrack.core.domain.data.CategoryTest.educationCategory
import com.zacle.spendtrack.core.domain.data.CategoryTest.entertainmentCategory
import com.zacle.spendtrack.core.domain.data.CategoryTest.foodCategory
import com.zacle.spendtrack.core.domain.data.IncomeTest.bonus
import com.zacle.spendtrack.core.domain.data.IncomeTest.gift
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Exceptions
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

class UpdateIncomeUseCaseTest {
    private val incomeRepository = mock<IncomeRepository>()
    private val budgetRepository = mock<BudgetRepository>()
    private val useCase = UpdateIncomeUseCase(mock(), incomeRepository, budgetRepository)

    private val userId = "userId"
    private val period = Period()

    @Test
    fun `should update greater income and update category budget amount and remaining amount`() = runTest {
        val educationBudget = Budget(category = educationCategory, amount = 1000.0, remainingAmount = 500.0)
        val foodBudget = Budget(category = foodCategory, amount = 500.0, remainingAmount = 250.0)
        val entertainmentBudget = Budget(category = entertainmentCategory, amount = 300.0, remainingAmount = 300.0)
        val budgets = listOf(educationBudget, foodBudget, entertainmentBudget)

        whenever(incomeRepository.getIncome(userId, bonus.incomeId)).thenReturn(flowOf(bonus))
        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(budgets))

        val request = UpdateIncomeUseCase.Request(userId, bonus.copy(amount = 600.0), period)
        useCase.process(request).first()

        verify(budgetRepository).updateBudget(userId, educationBudget.copy(amount = 1100.0, remainingAmount = 600.0))
    }

    @Test
    fun `should update lesser income and update category budget amount and remaining amount`() = runTest {
        val educationBudget = Budget(category = educationCategory, amount = 1000.0, remainingAmount = 500.0)
        val foodBudget = Budget(category = foodCategory, amount = 500.0, remainingAmount = 250.0)
        val entertainmentBudget = Budget(category = entertainmentCategory, amount = 300.0, remainingAmount = 300.0)
        val budgets = listOf(educationBudget, foodBudget, entertainmentBudget)

        whenever(incomeRepository.getIncome(userId, bonus.incomeId)).thenReturn(flowOf(bonus))
        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(budgets))

        val request = UpdateIncomeUseCase.Request(userId, bonus.copy(amount = 300.0), period)
        useCase.process(request).first()

        verify(budgetRepository).updateBudget(userId, educationBudget.copy(amount = 800.0, remainingAmount = 300.0))
    }

    @Test
    fun `should not update remaining amount if income amount is the same`() = runTest {
        val educationBudget = Budget(category = educationCategory, amount = 1000.0, remainingAmount = 500.0)
        val foodBudget = Budget(category = foodCategory, amount = 500.0, remainingAmount = 250.0)
        val entertainmentBudget = Budget(category = entertainmentCategory, amount = 300.0, remainingAmount = 300.0)
        val budgets = listOf(educationBudget, foodBudget, entertainmentBudget)

        whenever(incomeRepository.getIncome(userId, bonus.incomeId)).thenReturn(flowOf(bonus))
        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(budgets))

        val request = UpdateIncomeUseCase.Request(userId, bonus.copy(amount = 500.0), period)
        useCase.process(request).first()

        verify(budgetRepository).updateBudget(userId, educationBudget.copy(amount = 1000.0, remainingAmount = 500.0))
    }

    @Test
    fun `should throw an error if income does not exist`() = runTest {
        val educationBudget = Budget(category = educationCategory, amount = 1000.0, remainingAmount = 500.0)
        val foodBudget = Budget(category = foodCategory, amount = 500.0, remainingAmount = 250.0)
        val entertainmentBudget = Budget(category = entertainmentCategory, amount = 300.0, remainingAmount = 300.0)
        val budgets = listOf(educationBudget, foodBudget, entertainmentBudget)

        whenever(incomeRepository.getIncome(userId, bonus.incomeId)).thenReturn(flowOf(null))
        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(budgets))

        val request = UpdateIncomeUseCase.Request(userId, bonus.copy(amount = 500.0), period)

        try {
            useCase.process(request).first()
        } catch (e: Exception) {
            assertTrue(e is Exceptions.IncomeNotFoundException)
        }
    }

    @Test
    fun `should throw an error if category budget does not exist`() = runTest {
        val educationBudget = Budget(category = educationCategory, amount = 1000.0, remainingAmount = 500.0)
        val foodBudget = Budget(category = foodCategory, amount = 500.0, remainingAmount = 250.0)
        val entertainmentBudget = Budget(category = entertainmentCategory, amount = 300.0, remainingAmount = 300.0)
        val budgets = listOf(educationBudget, foodBudget, entertainmentBudget)

        whenever(incomeRepository.getIncome(userId, gift.incomeId)).thenReturn(flowOf(gift))
        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(budgets))

        val request = UpdateIncomeUseCase.Request(userId, gift.copy(amount = 500.0), period)

        try {
            useCase.process(request).first()
        } catch (e: Exception) {
            assertTrue(e is Exceptions.BudgetNotFoundException)
        }
    }
}