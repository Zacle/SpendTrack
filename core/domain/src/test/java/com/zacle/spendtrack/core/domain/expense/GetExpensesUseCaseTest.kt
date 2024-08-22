package com.zacle.spendtrack.core.domain.expense

import com.zacle.spendtrack.core.domain.data.ExpenseTest.library
import com.zacle.spendtrack.core.domain.data.ExpenseTest.restaurant
import com.zacle.spendtrack.core.domain.data.ExpenseTest.shopping
import com.zacle.spendtrack.core.domain.data.ExpenseTest.subscription
import com.zacle.spendtrack.core.domain.data.ExpenseTest.travel
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class GetExpensesUseCaseTest {
    private val expenseRepository = mock<ExpenseRepository>()
    private val useCase = GetExpensesUseCase(mock(), expenseRepository)

    private val userId = "userId"
    private val period = Period()

    @Test
    fun `should return expenses for the given period`() = runTest {
        val expenses = listOf(restaurant, shopping, subscription, travel, library)
        whenever(expenseRepository.getExpenses(userId, period)).thenReturn(flowOf(expenses))
        val request = GetExpensesUseCase.Request(userId, emptySet(), period)
        val response = useCase.process(request).first()
        assertEquals(
            GetExpensesUseCase.Response(
                amountSpent = expenses.sumOf { it.amount },
                expenses = expenses
            ),
            response
        )
    }

    @Test
    fun `should return expenses for the given categories`() = runTest {
        val expenses = listOf(restaurant, shopping, subscription, travel, library)
        whenever(expenseRepository.getExpenses(userId, period)).thenReturn(flowOf(expenses))
        val request = GetExpensesUseCase
            .Request(
                userId,
                setOf(shopping.category.categoryId, subscription.category.categoryId, travel.category.categoryId),
                period
            )
        val response = useCase.process(request).first()
        val list = listOf(shopping, subscription, travel)
        assertEquals(
            GetExpensesUseCase.Response(
                amountSpent = list.sumOf { it.amount },
                expenses = list
            ),
            response
        )
    }
}