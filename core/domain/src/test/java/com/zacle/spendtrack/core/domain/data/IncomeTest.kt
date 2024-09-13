package com.zacle.spendtrack.core.domain.data

import com.zacle.spendtrack.core.model.Income
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

object IncomeTest {
    val bonus = Income(
        id = "2",
        amount = 500.0,
        category = CategoryTest.educationCategory,
        transactionDate = Clock.System.now()
    )
    val gift = Income(
        id = "3",
        amount = 200.0,
        category = CategoryTest.shoppingCategory,
        transactionDate = Clock.System.now().plus(3.hours)
    )
    val internship = Income(
        id = "4",
        amount = 100.0,
        category = CategoryTest.educationCategory,
        transactionDate = Clock.System.now().plus(1.days)
    )
    val refund = Income(
        id = "5",
        amount = 50.0,
        category = CategoryTest.foodCategory,
        transactionDate = Clock.System.now().plus(2.days)
    )
}