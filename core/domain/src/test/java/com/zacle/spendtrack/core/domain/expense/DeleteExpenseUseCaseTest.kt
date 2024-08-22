package com.zacle.spendtrack.core.domain.expense

import com.zacle.spendtrack.core.domain.data.CategoryTest.educationCategory
import com.zacle.spendtrack.core.domain.data.CategoryTest.shoppingCategory
import com.zacle.spendtrack.core.domain.data.CategoryTest.travelCategory
import com.zacle.spendtrack.core.domain.data.ExpenseTest.library
import com.zacle.spendtrack.core.domain.data.ExpenseTest.restaurant
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

class DeleteExpenseUseCaseTest {
    private val expenseRepository = mock<ExpenseRepository>()
    private val budgetRepository = mock<BudgetRepository>()
    private val deleteExpenseUseCase = DeleteExpenseUseCase(mock(), expenseRepository, budgetRepository)

    private val userId = "userId"
    private val period = Period()

    @Test
    fun `should not delete the expense if category budget does not exist`() = runTest {
        val educationBudget = Budget(amount = 100.0, remainingAmount = 100.0, category = educationCategory)
        val shoppingBudget = Budget(amount = 200.0, remainingAmount = 200.0, category = shoppingCategory)
        val travelBudget = Budget(amount = 300.0, remainingAmount = 300.0, category = travelCategory)

        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(listOf(educationBudget, shoppingBudget, travelBudget)))

        try {
            deleteExpenseUseCase.process(DeleteExpenseUseCase.Request(userId, restaurant, period)).first()
        } catch (e: Exception) {
            assertTrue(e is Exceptions.BudgetNotFoundException)
        }
    }

    @Test
    fun `should delete expense and update category budget remaining amount`() = runTest {
        val educationBudget = Budget(amount = 100.0, remainingAmount = 20.0, category = educationCategory)
        val shoppingBudget = Budget(amount = 200.0, remainingAmount = 200.0, category = shoppingCategory)
        val travelBudget = Budget(amount = 300.0, remainingAmount = 300.0, category = travelCategory)

        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(listOf(educationBudget, shoppingBudget, travelBudget)))

        deleteExpenseUseCase.process(DeleteExpenseUseCase.Request(userId, library, period)).first()

        verify(budgetRepository).updateBudget(userId, educationBudget.copy(remainingAmount = 70.0))
    }
}