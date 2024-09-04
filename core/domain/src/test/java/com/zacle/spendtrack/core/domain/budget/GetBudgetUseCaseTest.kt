package com.zacle.spendtrack.core.domain.budget

import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Exceptions
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetBudgetUseCaseTest {
    private val budgetRepository = mock<BudgetRepository>()
    private val useCase = GetBudgetUseCase(mock(), budgetRepository)

    private val userId = "userId"

    @Test
    fun `should get the budget with the given id`() = runTest {
        val period = Period()
        val request = GetBudgetUseCase.Request(userId, "budgetId")

        whenever(budgetRepository.getBudget(userId, "budgetId")).thenReturn(flowOf(Budget()))

        val budget = useCase.process(request).first()

        assertNotNull(budget)
    }

    @Test
    fun `should throw an exception if the budget is not found`() = runTest {
        val request = GetBudgetUseCase.Request(userId, "budgetId")

        whenever(budgetRepository.getBudget(userId, "budgetId")).thenReturn(flowOf(null))

        try {
            useCase.process(request).first()
        } catch (e: Exception) {
            assertTrue(e is Exceptions.BudgetNotFoundException)
        }
    }
}