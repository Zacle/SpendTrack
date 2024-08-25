package com.zacle.spendtrack.core.domain.budget

import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.model.Budget
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class DeleteBudgetUseCaseTest {
    private val budgetRepository = mock<BudgetRepository>()
    private val useCase = DeleteBudgetUseCase(mock(), budgetRepository)

    @Test
    fun `should delete a budget`() = runTest {
        val userId = "userId"
        val budget = Budget()

        useCase.process(DeleteBudgetUseCase.Request(userId, budget)).first()

        verify(budgetRepository).deleteBudget(budget)

    }
}