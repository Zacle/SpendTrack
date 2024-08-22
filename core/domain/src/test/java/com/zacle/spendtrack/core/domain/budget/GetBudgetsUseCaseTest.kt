package com.zacle.spendtrack.core.domain.budget

import com.zacle.spendtrack.core.domain.data.CategoryTest.educationCategory
import com.zacle.spendtrack.core.domain.data.CategoryTest.foodCategory
import com.zacle.spendtrack.core.domain.data.CategoryTest.shoppingCategory
import com.zacle.spendtrack.core.domain.data.CategoryTest.travelCategory
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

class GetBudgetsUseCaseTest {
    private val budgetRepository = mock<BudgetRepository>()
    private val useCase = GetBudgetsUseCase(mock(), budgetRepository)

    private val userId = "userId"

    @Test
    fun `should return a total balance of zero if no budget has been set`() = runTest {
        val period = Period()
        val request = GetBudgetsUseCase.Request(userId, period)

        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(emptyList()))

        val budgets = useCase.process(request).first()

        assertEquals(
            GetBudgetsUseCase.Response(0.0, 0.0, emptyList()),
            budgets
        )
    }

    @Test
    fun `should return a total balance of the sum of all budgets`() = runTest {
        val period = Period()
        val request = GetBudgetsUseCase.Request(userId, period)
        val budget1 = Budget(amount = 100.0, category = foodCategory, remainingAmount = 100.0)
        val budget2 = Budget(amount = 200.0, category = educationCategory, remainingAmount = 200.0)
        val budget3 = Budget(amount = 300.0, category = shoppingCategory, remainingAmount = 300.0)

        val budgets = listOf(budget1, budget2, budget3)

        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(budgets))

        val response = useCase.process(request).first()

        assertEquals(
            GetBudgetsUseCase.Response(600.0, 600.0, budgets),
            response
        )
    }

    @Test
    fun `should return a total balance of the sum of all budgets with remaining amount`() = runTest {
        val period = Period()
        val request = GetBudgetsUseCase.Request(userId, period)
        val budget1 = Budget(amount = 100.0, category = foodCategory, remainingAmount = -100.0)
        val budget2 = Budget(amount = 200.0, category = educationCategory, remainingAmount = 200.0)
        val budget3 = Budget(amount = 300.0, category = shoppingCategory, remainingAmount = 200.0)
        val budget4 = Budget(amount = 400.0, category = travelCategory, remainingAmount = 100.0)

        val budgets = listOf(budget1, budget2, budget3, budget4)

        whenever(budgetRepository.getBudgets(userId, period)).thenReturn(flowOf(budgets))

        val response = useCase.process(request).first()

        assertEquals(
            GetBudgetsUseCase.Response(1000.0, 500.0, budgets),
            response
        )
    }
}