package com.zacle.spendtrack.core.domain.expense

import com.zacle.spendtrack.core.domain.data.CategoryTest.entertainmentCategory
import com.zacle.spendtrack.core.domain.data.CategoryTest.foodCategory
import com.zacle.spendtrack.core.domain.data.CategoryTest.shoppingCategory
import com.zacle.spendtrack.core.domain.data.ExpenseTest.subscription
import com.zacle.spendtrack.core.domain.data.ExpenseTest.travel
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Exceptions
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertTrue

class AddExpenseUseCaseTest {
    private val expenseRepository = mock<ExpenseRepository>()
    private val budgetRepository = mock<BudgetRepository>()
    private val addExpenseUseCase = AddExpenseUseCase(mock(), expenseRepository, budgetRepository)

    private val userId = "userId"
    private val period = Period()

    @Test
    fun `should throw an error if category budget does not exist`() = runTest {
        val foodBudget = Budget(category = foodCategory, amount = 100.0, remainingAmount = 50.0)
        val shoppingBudget = Budget(category = shoppingCategory, amount = 200.0, remainingAmount = 200.0)

        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(listOf(foodBudget, shoppingBudget)))

        val request = AddExpenseUseCase.Request(userId, travel, period)

        try {
            addExpenseUseCase.process(request).first()
        } catch (e: Exception) {
            assertTrue(e is Exceptions.CategoryBudgetNotExistsException)
        }
    }

    @Test
    fun `should add expense and update category budget remaining amount`() = runTest {
        val foodBudget = Budget(category = foodCategory, amount = 100.0, remainingAmount = 50.0)
        val shoppingBudget = Budget(category = shoppingCategory, amount = 200.0, remainingAmount = 200.0)
        val entertainmentBudget = Budget(category = entertainmentCategory, amount = 300.0, remainingAmount = 300.0)

        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(listOf(foodBudget, shoppingBudget, entertainmentBudget)))

        val request = AddExpenseUseCase.Request(userId, subscription, period)
        addExpenseUseCase.process(request).first()

        verify(budgetRepository).updateBudget(entertainmentBudget.copy(remainingAmount = 270.0))
    }
}