package com.zacle.spendtrack.core.database.dao

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.zacle.spendtrack.core.database.STDatabase
import com.zacle.spendtrack.core.database.TestDispatcherRule
import com.zacle.spendtrack.core.database.dao.BudgetData.dailyPeriod
import com.zacle.spendtrack.core.database.dao.BudgetData.monthlyPeriod
import com.zacle.spendtrack.core.database.dao.BudgetData.weeklyPeriod
import com.zacle.spendtrack.core.database.dao.IncomeData.bonus
import com.zacle.spendtrack.core.database.dao.IncomeData.foodIncome
import com.zacle.spendtrack.core.database.dao.IncomeData.other
import com.zacle.spendtrack.core.database.dao.IncomeData.refund
import com.zacle.spendtrack.core.database.dao.IncomeData.salary
import com.zacle.spendtrack.core.database.model.CATEGORIES
import com.zacle.spendtrack.core.database.model.IncomeEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.days

@HiltAndroidTest
@SmallTest
class IncomeDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @Inject
    @Named("test_db")
    lateinit var database: STDatabase

    private lateinit var incomeDao: IncomeDao
    private lateinit var categoryDao: CategoryDao

    @Before
    fun setup() {
        hiltRule.inject()
        incomeDao = database.incomeDao()
        categoryDao = database.categoryDao()
        runBlocking {
            categoryDao.insertAll(CATEGORIES)
        }
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun noIncomes() = runTest {
        val (start, end) = dailyPeriod()
        val incomes = incomeDao.getIncomes("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(incomes).isEmpty()
    }

    @Test
    fun shouldRetrieveOneIncome() = runTest {
        val (start, end) = weeklyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodIncome.copy(categoryId = categories.random().id)
        incomeDao.insertIncome(food)
        val incomes = incomeDao.getIncomes("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(incomes).hasSize(1)
    }

    @Test
    fun shouldRetrieveMultipleIncomes() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodIncome.copy(categoryId = categories.random().id)
        val refund = refund.copy(categoryId = categories.random().id)
        val salary = salary.copy(categoryId = categories.random().id)
        incomeDao.insertIncome(food)
        incomeDao.insertIncome(refund)
        incomeDao.insertIncome(salary)
        val incomes = incomeDao.getIncomes("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(incomes).hasSize(3)
    }

    @Test
    fun shouldNotRetrieveIncomeOutsidePeriod() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodIncome.copy(categoryId = categories.random().id)
        val refund = refund.copy(categoryId = categories.random().id)
        val salary = salary.copy(categoryId = categories.random().id)
        val other = other.copy(categoryId = categories.random().id)
        incomeDao.insertIncome(food)
        incomeDao.insertIncome(refund)
        incomeDao.insertIncome(salary)
        incomeDao.insertIncome(other)
        val incomes = incomeDao.getIncomes("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(incomes).hasSize(3)
    }

    @Test
    fun shouldRetrieveAnExistingIncome() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodIncome.copy(categoryId = categories.random().id)
        incomeDao.insertIncome(food)
        val income = incomeDao.getIncome("1", food.incomeId).first()
        assertThat(income).isNotNull()
    }

    @Test
    fun shouldNotRetrieveANonExistingIncome() = runTest {
        val categories = categoryDao.getAllCategories().first()
        val food = foodIncome.copy(categoryId = categories.random().id)
        incomeDao.insertIncome(food)
        val income = incomeDao.getIncome("1", "3").first()
        assertThat(income).isNull()
    }

    @Test
    fun shouldRetrieveIncomesByCategory() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodIncome.copy(categoryId = categories[1].id)
        val refund = refund.copy(categoryId = categories[1].id)
        val salary = salary.copy(categoryId = categories[2].id)
        val bonus = bonus.copy(categoryId = categories[1].id)
        incomeDao.insertIncome(food)
        incomeDao.insertIncome(refund)
        incomeDao.insertIncome(salary)
        incomeDao.insertIncome(bonus)
        val incomes = incomeDao.getIncomesByCategory("1", categories[1].id, start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(incomes).hasSize(2)
    }

    @Test
    fun shouldNotRetrieveIncomesByNonExistingCategory() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodIncome.copy(categoryId = categories[1].id)
        val refund = refund.copy(categoryId = categories[1].id)
        val salary = salary.copy(categoryId = categories[2].id)
        val bonus = bonus.copy(categoryId = categories[1].id)
        incomeDao.insertIncome(food)
        incomeDao.insertIncome(refund)
        incomeDao.insertIncome(salary)
        incomeDao.insertIncome(bonus)
        val incomes = incomeDao.getIncomesByCategory("1", categories[5].id, start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(incomes).hasSize(0)
    }

    @Test
    fun shouldDeleteIncome() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodIncome.copy(categoryId = categories[1].id)
        val refund = refund.copy(categoryId = categories[1].id)
        val salary = salary.copy(categoryId = categories[2].id)
        incomeDao.insertIncome(food)
        incomeDao.insertIncome(refund)
        incomeDao.insertIncome(salary)
        incomeDao.deleteIncome(food)
        val incomes = incomeDao.getIncomes("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(incomes).hasSize(2)
    }
}

object IncomeData {
    val foodIncome = IncomeEntity(
        incomeId = "1",
        userId = "1",
        categoryId = "1",
        transactionDate = Clock.System.now(),
        name = "Food",
        amount = 100.0,
    )
    val refund = IncomeEntity(
        incomeId = "2",
        userId = "1",
        categoryId = "2",
        transactionDate = Clock.System.now().plus(1.days),
        name = "Refund",
        amount = 200.0,
    )
    val salary = IncomeEntity(
        incomeId = "3",
        userId = "1",
        categoryId = "3",
        transactionDate = Clock.System.now().plus(2.days),
        name = "Salary",
        amount = 300.0,
    )
    val bonus = IncomeEntity(
        incomeId = "4",
        userId = "1",
        categoryId = "4",
        transactionDate = Clock.System.now().plus(10.days),
        name = "Bonus",
        amount = 400.0,
    )
    val other = IncomeEntity(
        incomeId = "5",
        userId = "1",
        categoryId = "5",
        transactionDate = Clock.System.now().plus(31.days),
        name = "Other",
        amount = 500.0
    )
}