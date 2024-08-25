package com.zacle.spendtrack.core.database.dao

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.zacle.spendtrack.core.database.STDatabase
import com.zacle.spendtrack.core.database.TestDispatcherRule
import com.zacle.spendtrack.core.database.dao.BudgetData.dailyPeriod
import com.zacle.spendtrack.core.database.dao.BudgetData.monthlyPeriod
import com.zacle.spendtrack.core.database.dao.BudgetData.weeklyPeriod
import com.zacle.spendtrack.core.database.dao.ExpenseData.educationExpense
import com.zacle.spendtrack.core.database.dao.ExpenseData.entertainmentExpense
import com.zacle.spendtrack.core.database.dao.ExpenseData.foodExpense
import com.zacle.spendtrack.core.database.dao.ExpenseData.shoppingExpense
import com.zacle.spendtrack.core.database.dao.ExpenseData.travelExpense
import com.zacle.spendtrack.core.database.model.CATEGORIES
import com.zacle.spendtrack.core.database.model.ExpenseEntity
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
class ExpenseDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @Inject
    @Named("test_db")
    lateinit var database: STDatabase

    private lateinit var expenseDao: ExpenseDao
    private lateinit var categoryDao: CategoryDao

    @Before
    fun setup() {
        hiltRule.inject()
        expenseDao = database.expenseDao()
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
    fun noExpenses() = runTest {
        val (start, end) = dailyPeriod()
        val expenses = expenseDao.getExpenses("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(expenses).isEmpty()
    }

    @Test
    fun shouldRetrieveOneExpense() = runTest {
        val (start, end) = weeklyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodExpense.copy(categoryId = categories.random().id)
        expenseDao.insertExpense(food)
        val expenses = expenseDao.getExpenses("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(expenses).hasSize(1)
    }

    @Test
    fun shouldRetrieveMultipleExpenses() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodExpense.copy(categoryId = categories.random().id)
        val shopping = shoppingExpense.copy(categoryId = categories.random().id)
        val entertainment = entertainmentExpense.copy(categoryId = categories.random().id)
        expenseDao.insertExpense(food)
        expenseDao.insertExpense(shopping)
        expenseDao.insertExpense(entertainment)
        val expenses = expenseDao.getExpenses("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(expenses).hasSize(3)
    }

    @Test
    fun shouldNotRetrieveExpenseOutsidePeriod() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodExpense.copy(categoryId = categories.random().id)
        val shopping = shoppingExpense.copy(categoryId = categories.random().id)
        val entertainment = entertainmentExpense.copy(categoryId = categories.random().id)
        val travel = travelExpense.copy(categoryId = categories.random().id)
        expenseDao.insertExpense(food)
        expenseDao.insertExpense(shopping)
        expenseDao.insertExpense(entertainment)
        expenseDao.insertExpense(travel)
        val expenses = expenseDao.getExpenses("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(expenses).hasSize(3)
    }

    @Test
    fun shouldRetrieveAnExistingExpense() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodExpense.copy(categoryId = categories.random().id)
        expenseDao.insertExpense(food)
        val expense = expenseDao.getExpense("1", food.expenseId).first()
        assertThat(expense).isNotNull()
    }

    @Test
    fun shouldNotRetrieveANonExistingExpense() = runTest {
        val categories = categoryDao.getAllCategories().first()
        val food = foodExpense.copy(categoryId = categories.random().id)
        expenseDao.insertExpense(food)
        val expense = expenseDao.getExpense("1", "3").first()
        assertThat(expense).isNull()
    }

    @Test
    fun shouldRetrieveExpensesByCategory() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodExpense.copy(categoryId = categories[1].id)
        val shopping = shoppingExpense.copy(categoryId = categories[1].id)
        val entertainment = entertainmentExpense.copy(categoryId = categories[2].id)
        val education = educationExpense.copy(categoryId = categories[1].id)
        expenseDao.insertExpense(food)
        expenseDao.insertExpense(shopping)
        expenseDao.insertExpense(entertainment)
        expenseDao.insertExpense(education)
        val expenses = expenseDao.getExpensesByCategory("1", categories[1].id, start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(expenses).hasSize(2)
    }

    @Test
    fun shouldNotRetrieveExpensesByNonExistingCategory() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodExpense.copy(categoryId = categories[1].id)
        val shopping = shoppingExpense.copy(categoryId = categories[1].id)
        val entertainment = entertainmentExpense.copy(categoryId = categories[2].id)
        val education = educationExpense.copy(categoryId = categories[1].id)
        expenseDao.insertExpense(food)
        expenseDao.insertExpense(shopping)
        expenseDao.insertExpense(entertainment)
        expenseDao.insertExpense(education)
        val expenses = expenseDao.getExpensesByCategory("1", categories[5].id, start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(expenses).hasSize(0)
    }

    @Test
    fun shouldDeleteExpense() = runTest {
        val (start, end) = monthlyPeriod()
        val categories = categoryDao.getAllCategories().first()
        val food = foodExpense.copy(categoryId = categories[1].id)
        val shopping = shoppingExpense.copy(categoryId = categories[1].id)
        val entertainment = entertainmentExpense.copy(categoryId = categories[2].id)
        expenseDao.insertExpense(food)
        expenseDao.insertExpense(shopping)
        expenseDao.insertExpense(entertainment)
        expenseDao.deleteExpense(food)
        val expenses = expenseDao.getExpenses("1", start.toEpochMilliseconds(), end.toEpochMilliseconds()).first()
        assertThat(expenses).hasSize(2)
    }
}

/**
 * Test data for expenses
 */
object ExpenseData {
    val foodExpense = ExpenseEntity(
        expenseId = "1",
        userId = "1",
        categoryId = "1",
        transactionDate = Clock.System.now(),
        name = "Food",
        amount = 100.0,
    )
    val shoppingExpense = ExpenseEntity(
        expenseId = "2",
        userId = "1",
        categoryId = "2",
        transactionDate = Clock.System.now().plus(1.days),
        name = "Shopping",
        amount = 200.0,
    )
    val entertainmentExpense = ExpenseEntity(
        expenseId = "3",
        userId = "1",
        categoryId = "3",
        transactionDate = Clock.System.now().plus(2.days),
        name = "Entertainment",
        amount = 300.0,
    )
    val travelExpense = ExpenseEntity(
        expenseId = "4",
        userId = "1",
        categoryId = "4",
        transactionDate = Clock.System.now().plus(10.days),
        name = "Travel",
        amount = 400.0,
    )
    val educationExpense = ExpenseEntity(
        expenseId = "5",
        userId = "1",
        categoryId = "5",
        transactionDate = Clock.System.now().plus(31.days),
        name = "Education",
        amount = 500.0,
    )

}