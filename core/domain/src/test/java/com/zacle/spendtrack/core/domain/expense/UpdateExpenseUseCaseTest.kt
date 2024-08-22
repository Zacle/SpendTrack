package com.zacle.spendtrack.core.domain.expense

import com.zacle.spendtrack.core.domain.data.CategoryTest.entertainmentCategory
import com.zacle.spendtrack.core.domain.data.CategoryTest.foodCategory
import com.zacle.spendtrack.core.domain.data.CategoryTest.shoppingCategory
import com.zacle.spendtrack.core.domain.data.ExpenseTest.shopping
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Exceptions
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertTrue

class UpdateExpenseUseCaseTest {
    private val expenseRepository = mock<ExpenseRepository>()
    private val budgetRepository = mock<BudgetRepository>()
    private val useCase = UpdateExpenseUseCase(mock(), expenseRepository, budgetRepository)

    private val userId = "userId"
    private val period = Period()

    @Test
    fun `should update expense and update category budget remaining amount`() = runTest {
        val foodBudget = Budget(category = foodCategory, amount = 100.0, remainingAmount = 50.0)
        val shoppingBudget = Budget(category = shoppingCategory, amount = 200.0, remainingAmount = 180.0)
        val entertainmentBudget = Budget(category = entertainmentCategory, amount = 300.0, remainingAmount = 300.0)

        whenever(expenseRepository.getExpense(userId, shopping.expenseId)).thenReturn(flowOf(shopping))
        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(listOf(foodBudget, shoppingBudget, entertainmentBudget)))

        val request = UpdateExpenseUseCase.Request(userId, shopping.copy(amount = 50.0), period)

        useCase.process(request).first()

        verify(budgetRepository).updateBudget(userId, shoppingBudget.copy(remainingAmount = 150.0))
    }

    @Test
    fun `should not update remaining amount if expense amount is the same`() = runTest {
        val foodBudget = Budget(category = foodCategory, amount = 100.0, remainingAmount = 50.0)
        val shoppingBudget = Budget(category = shoppingCategory, amount = 200.0, remainingAmount = 180.0)
        val entertainmentBudget = Budget(category = entertainmentCategory, amount = 300.0, remainingAmount = 300.0)

        whenever(expenseRepository.getExpense(userId, shopping.expenseId)).thenReturn(flowOf(shopping))
        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(listOf(foodBudget, shoppingBudget, entertainmentBudget)))

        val request = UpdateExpenseUseCase.Request(userId, shopping.copy(amount = 20.0), period)

        useCase.process(request).first()

        verify(budgetRepository).updateBudget(userId, shoppingBudget.copy(remainingAmount = 180.0))
    }

    @Test
    fun `should update remaining amount if expense amount less than the previous`() = runTest {
        val foodBudget = Budget(category = foodCategory, amount = 100.0, remainingAmount = 50.0)
        val shoppingBudget = Budget(category = shoppingCategory, amount = 200.0, remainingAmount = 180.0)
        val entertainmentBudget = Budget(category = entertainmentCategory, amount = 300.0, remainingAmount = 300.0)

        whenever(expenseRepository.getExpense(userId, shopping.expenseId)).thenReturn(flowOf(shopping))
        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(listOf(foodBudget, shoppingBudget, entertainmentBudget)))

        val request = UpdateExpenseUseCase.Request(userId, shopping.copy(amount = 10.0), period)

        useCase.process(request).first()

        verify(budgetRepository).updateBudget(userId, shoppingBudget.copy(remainingAmount = 190.0))
    }

    @Test
    fun `should throw an error if expense does not exist`() = runTest {
        val foodBudget = Budget(category = foodCategory, amount = 100.0, remainingAmount = 50.0)
        val shoppingBudget = Budget(category = shoppingCategory, amount = 200.0, remainingAmount = 180.0)
        val entertainmentBudget = Budget(category = entertainmentCategory, amount = 300.0, remainingAmount = 300.0)

        whenever(expenseRepository.getExpense(userId, shopping.expenseId)).thenReturn(flowOf(null))
        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(listOf(foodBudget, shoppingBudget, entertainmentBudget)))

        try {
            useCase.process(UpdateExpenseUseCase.Request(userId, shopping, period)).first()
        } catch (e: Exception) {
            assertTrue(e is Exceptions.ExpenseNotFoundException)
        }
    }
}