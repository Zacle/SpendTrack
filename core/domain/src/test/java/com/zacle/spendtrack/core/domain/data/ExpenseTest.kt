package com.zacle.spendtrack.core.domain.data

import com.zacle.spendtrack.core.model.Expense
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

object ExpenseTest {
    val restaurant = Expense(
        id = "1",
        amount = 10.0,
        category = CategoryTest.foodCategory,
        transactionDate = Clock.System.now().plus(50.minutes)
    )
    val shopping = Expense(
        id = "2",
        amount = 20.0,
        category = CategoryTest.shoppingCategory,
        transactionDate = Clock.System.now().plus(1.days)
    )
    val subscription = Expense(
        id = "3",
        amount = 30.0,
        category = CategoryTest.entertainmentCategory,
        transactionDate = Clock.System.now().plus(6.hours)
    )
    val travel = Expense(
        id = "4",
        amount = 40.0,
        category = CategoryTest.travelCategory,
        transactionDate = Clock.System.now().minus(2.days)
    )
    val library = Expense(
        id = "5",
        amount = 50.0,
        category = CategoryTest.educationCategory,
        transactionDate = Clock.System.now().plus(2.days)
    )
}