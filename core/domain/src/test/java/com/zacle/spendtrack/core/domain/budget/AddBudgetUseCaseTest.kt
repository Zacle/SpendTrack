package com.zacle.spendtrack.core.domain.budget

import com.zacle.spendtrack.core.domain.data.CategoryTest.entertainmentCategory
import com.zacle.spendtrack.core.domain.data.CategoryTest.foodCategory
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class AddBudgetUseCaseTest {
    private val budgetRepository = mock<BudgetRepository>()
    private val useCase = AddBudgetUseCase(mock(), budgetRepository)

    private val userId = "userId"

    @Test
    fun `should add a budget if it does not yet exist`() = runTest {
        val category = foodCategory
        val budget = Budget(category = category, amount = 100.0)
        val period = Period()

        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(emptyList()))

        val response = useCase.process(AddBudgetUseCase.Request(userId, budget, period)).first()

        assertEquals(response.budget.amount, 100.0)
        assertEquals(response.budget.remainingAmount, 100.0)
    }

    @Test
    fun `should update a budget if it already exists`() = runTest {
        val category = foodCategory
        val budget = Budget(category = category, amount = 100.0, remainingAmount = 50.0)
        val newBudget = Budget(category = category, amount = 200.0)
        val period = Period()

        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(listOf(budget)))

        val response = useCase.process(AddBudgetUseCase.Request(userId, newBudget, period)).first()

        assertEquals(response.budget.amount, 300.0)
        assertEquals(response.budget.remainingAmount, 250.0)

    }

    @Test
    fun `should be able to add different budgets for different categories`() = runTest {
        val category = foodCategory
        val budget = Budget(category = category, amount = 100.0)
        val newBudget = Budget(category = entertainmentCategory, amount = 50.0)
        val period = Period()

        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(listOf(budget)))

        val response = useCase.process(AddBudgetUseCase.Request(userId, newBudget, period)).first()

        assertEquals(response.budget.amount, 50.0)
        assertEquals(response.budget.remainingAmount, 50.0)
    }
}