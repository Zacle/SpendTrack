package com.zacle.spendtrack.core.database.dao

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.zacle.spendtrack.core.database.STDatabase
import com.zacle.spendtrack.core.database.TestDispatcherRule
import com.zacle.spendtrack.core.database.dao.BudgetData.dailyPeriod
import com.zacle.spendtrack.core.database.dao.BudgetData.educationBudget
import com.zacle.spendtrack.core.database.dao.BudgetData.entertainmentBudget
import com.zacle.spendtrack.core.database.dao.BudgetData.foodBudget
import com.zacle.spendtrack.core.database.dao.BudgetData.monthlyPeriod
import com.zacle.spendtrack.core.database.dao.BudgetData.shoppingBudget
import com.zacle.spendtrack.core.database.dao.BudgetData.travelBudget
import com.zacle.spendtrack.core.database.dao.BudgetData.weeklyPeriod
import com.zacle.spendtrack.core.database.dao.BudgetData.yearlyPeriod
import com.zacle.spendtrack.core.database.model.BudgetEntity
import com.zacle.spendtrack.core.database.model.CATEGORIES
import com.zacle.spendtrack.core.model.Period
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.toKotlinInstant
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.days

@HiltAndroidTest
@SmallTest
class BudgetDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @Inject
    @Named("test_db")
    lateinit var database: STDatabase

    private lateinit var budgetDao: BudgetDao
    private lateinit var categoryDao: CategoryDao

    @Before
    fun setup() {
        hiltRule.inject()
        budgetDao = database.budgetDao()
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
    fun noBudgets() = runTest {
        val (start, end) = dailyPeriod()
        val budgets = budgetDao.getBudgets("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(budgets).isEmpty()
    }

    @Test
    fun insertAndRetrieveAListOfOneBudget() = runTest {
        val (start, end) = dailyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val category = categories.random()
        val budget = foodBudget.copy(categoryId = category.id)
        budgetDao.insertBudget(budget)
        val budgets = budgetDao.getBudgets("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(budgets).hasSize(1)
    }

    @Test
    fun insertAndRetrieveAListOfMultipleBudgets() = runTest {
        val (start, end) = yearlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val foodBudget = foodBudget.copy(categoryId = categories[0].id)
        val educationBudget = educationBudget.copy(categoryId = categories[1].id)
        val entertainmentBudget = entertainmentBudget.copy(categoryId = categories[2].id)
        val shoppingBudget = shoppingBudget.copy(categoryId = categories[3].id)
        budgetDao.insertBudget(foodBudget)
        budgetDao.insertBudget(educationBudget)
        budgetDao.insertBudget(entertainmentBudget)
        budgetDao.insertBudget(shoppingBudget)
        val budgets = budgetDao.getBudgets("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(budgets).hasSize(4)
    }

    @Test
    fun retrieveWeeklyBudget() = runTest {
        val (start, end) = weeklyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val foodBudget = foodBudget.copy(categoryId = categories[0].id)
        val educationBudget = educationBudget.copy(categoryId = categories[1].id)
        val entertainmentBudget = entertainmentBudget.copy(categoryId = categories[2].id)
        val shoppingBudget = shoppingBudget.copy(categoryId = categories[3].id)
        val travelBudget = travelBudget.copy(categoryId = categories[4].id)
        budgetDao.insertBudget(foodBudget)
        budgetDao.insertBudget(educationBudget)
        budgetDao.insertBudget(entertainmentBudget)
        budgetDao.insertBudget(shoppingBudget)
        budgetDao.insertBudget(travelBudget)
        val budgets = budgetDao.getBudgets("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(budgets).hasSize(3)
    }

    @Test
    fun retrieveYearlyBudget() = runTest {
        val (start, end) = yearlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val foodBudget = foodBudget.copy(categoryId = categories[0].id)
        val educationBudget = educationBudget.copy(categoryId = categories[1].id)
        val entertainmentBudget = entertainmentBudget.copy(categoryId = categories[2].id)
        val shoppingBudget = shoppingBudget.copy(categoryId = categories[3].id)
        val travelBudget = travelBudget.copy(categoryId = categories[4].id)
        budgetDao.insertBudget(foodBudget)
        budgetDao.insertBudget(educationBudget)
        budgetDao.insertBudget(entertainmentBudget)
        budgetDao.insertBudget(shoppingBudget)
        budgetDao.insertBudget(travelBudget)
        val budgets = budgetDao.getBudgets("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(budgets).hasSize(5)
    }

    @Test
    fun shouldNotRetrieveBudgetOutsidePeriod() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val travelBudget = travelBudget.copy(categoryId = categories[4].id)
        budgetDao.insertBudget(travelBudget)
        val budget = budgetDao.getBudget("5", travelBudget.id).first()
        assertThat(budget).isNull()
    }

    @Test
    fun shouldReturnNullIfBudgetDoesNotExist() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val educationBudget = educationBudget.copy(categoryId = categories[1].id)
        budgetDao.insertBudget(educationBudget)
        val budget = budgetDao.getBudget("1", "1").first()
        assertThat(budget).isNull()
    }

    @Test
    fun shouldDeleteBudget() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val foodBudget = foodBudget.copy(categoryId = categories[0].id)
        val educationBudget = educationBudget.copy(categoryId = categories[1].id)
        budgetDao.insertBudget(foodBudget)
        budgetDao.insertBudget(educationBudget)
        budgetDao.deleteBudget(foodBudget.userId, foodBudget.id)
        val budgets = budgetDao.getBudgets("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(budgets).hasSize(1)
        assertThat(budgets[0].budget.id).isEqualTo(educationBudget.id)
    }

}


/**
 * Test data for budgets
 */
object BudgetData {
    private val currentDay = OffsetDateTime.now()

    private val currentDayMonthLength = LocalDate
        .of(currentDay.year, currentDay.month, currentDay.dayOfMonth)
        .lengthOfMonth()

    val foodBudget = BudgetEntity(
        id = "1",
        userId = "1",
        categoryId = "1",
        amount = 100.0,
        budgetPeriod = Clock.System.now(),
        createdAt = Clock.System.now()
    )
    val educationBudget = BudgetEntity(
        id = "2",
        userId = "1",
        categoryId = "2",
        amount = 200.0,
        budgetPeriod = Clock.System.now().plus(1.days)
    )
    val entertainmentBudget = BudgetEntity(
        id = "3",
        userId = "1",
        categoryId = "3",
        amount = 300.0,
        budgetPeriod = Clock.System.now().plus(2.days)
    )
    val shoppingBudget = BudgetEntity(
        id = "4",
        userId = "1",
        categoryId = "4",
        amount = 400.0,
        budgetPeriod = Clock.System.now().plus(10.days)
    )
    val travelBudget = BudgetEntity(
        id = "5",
        userId = "1",
        categoryId = "5",
        amount = 500.0,
        budgetPeriod = Clock.System.now().plus(31.days)
    )

    fun monthlyPeriod(): Period {
        val start = ZonedDateTime
            .now()
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0).toInstant().toKotlinInstant()
        val end = ZonedDateTime
            .now()
            .withDayOfMonth(currentDayMonthLength)
            .withHour(23)
            .withMinute(59).toInstant().toKotlinInstant()
        return Period(start, end)
    }

    fun dailyPeriod(): Period {
        val start = currentDay
            .withHour(0)
            .withMinute(0)
            .withSecond(0).toInstant().toKotlinInstant()
        val end = currentDay
            .withHour(23)
            .withMinute(59).toInstant().toKotlinInstant()
        return Period(start, end)
    }

    fun weeklyPeriod(): Period {
        val firstDayOfWeek: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val startTime = currentDay
            .with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
            .withHour(0)
            .withMinute(0)
            .withSecond(1)
        val endTime = startTime
            .plusDays(6)
            .withHour(23)
            .withMinute(59)
            .withSecond(59)
        return Period(startTime.toInstant().toKotlinInstant(), endTime.toInstant().toKotlinInstant())
    }

    fun yearlyPeriod(): Period {
        val start = ZonedDateTime
            .now()
            .withMonth(1)
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0).toInstant().toKotlinInstant()
        val end = ZonedDateTime
            .now()
            .withMonth(12)
            .withDayOfMonth(31)
            .withHour(23)
            .withMinute(59).toInstant().toKotlinInstant()
        return Period(start, end)
    }

}