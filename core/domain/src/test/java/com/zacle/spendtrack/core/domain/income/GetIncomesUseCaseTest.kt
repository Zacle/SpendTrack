package com.zacle.spendtrack.core.domain.income

import com.zacle.spendtrack.core.domain.data.IncomeTest.bonus
import com.zacle.spendtrack.core.domain.data.IncomeTest.gift
import com.zacle.spendtrack.core.domain.data.IncomeTest.internship
import com.zacle.spendtrack.core.domain.data.IncomeTest.refund
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class GetIncomesUseCaseTest {
    private val incomeRepository = mock<IncomeRepository>()
    private val useCase = GetIncomesUseCase(mock(), incomeRepository)

    private val userId = "userId"
    private val period = Period()

    @Test
    fun `should return incomes for the given period`() = runTest {
        val incomes = listOf(bonus, internship, gift, refund)
        val request = GetIncomesUseCase.Request(userId, emptySet(), period)
        whenever(incomeRepository.getIncomes(userId, period)).thenReturn(flowOf(incomes))
        val response = useCase.process(request).first()
        assertEquals(
            GetIncomesUseCase.Response(
                amountEarned = incomes.sumOf { it.amount },
                incomes = incomes
                ),
            response
        )
    }

    @Test
    fun `should return incomes for the given categories`() = runTest {
        val incomes = listOf(bonus, internship, gift, refund)
        val categoryIds = setOf(bonus.category.categoryId, internship.category.categoryId)
        val request = GetIncomesUseCase.Request(userId, categoryIds, period)
        whenever(incomeRepository.getIncomes(userId, period)).thenReturn(flowOf(incomes))
        val response = useCase.process(request).first()
        val expectedList = listOf(bonus, internship)
        assertEquals(
            GetIncomesUseCase.Response(
                amountEarned = expectedList.sumOf { it.amount },
                incomes = expectedList
            ),
            response
        )
    }
}