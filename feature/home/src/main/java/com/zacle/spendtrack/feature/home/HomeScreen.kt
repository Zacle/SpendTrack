package com.zacle.spendtrack.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.zacle.spendtrack.core.designsystem.component.BudgetCard
import com.zacle.spendtrack.core.designsystem.component.EmptyTransaction
import com.zacle.spendtrack.core.designsystem.component.PeriodPicker
import com.zacle.spendtrack.core.designsystem.component.STDropdown
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.TertiaryButton
import com.zacle.spendtrack.core.designsystem.component.TransactionAmountCard
import com.zacle.spendtrack.core.designsystem.component.TransactionCard
import com.zacle.spendtrack.core.designsystem.component.TransactionDateFilterChip
import com.zacle.spendtrack.core.designsystem.component.TransactionType
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.CommonScreen
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.ext.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeRoute(
    navigateToProfile: () -> Unit,
    navigateToBudgets: () -> Unit,
    navigateToTransactions: () -> Unit,
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit,
    navigateToBudgetDetails: (String) -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val homeUiStateHolder by viewModel.uiState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect { event ->
            when (event) {
                is HomeUiEvent.NavigateToLogin -> navigateToLogin()
                is HomeUiEvent.NavigateToProfile -> navigateToProfile()
                is HomeUiEvent.NavigateToBudgets -> navigateToBudgets()
                is HomeUiEvent.NavigateToTransactions -> navigateToTransactions()
                is HomeUiEvent.NavigateToExpense -> navigateToExpense(event.expenseId)
                is HomeUiEvent.NavigateToIncome -> navigateToIncome(event.incomeId)
                is HomeUiEvent.NavigateToBudgetDetails -> navigateToBudgetDetails(event.budgetId)
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        homeUiStateHolder = homeUiStateHolder,
        modelProducer = modelProducer,
        setPeriod = { viewModel.submitAction(HomeUiAction.SetPeriod(it)) },
        setDisplayTransactions = { viewModel.submitAction(HomeUiAction.SetDisplayTransactions(it)) },
        navigateToProfile = { viewModel.submitAction(HomeUiAction.NavigateToProfile) },
        navigateToExpense = { viewModel.submitAction(HomeUiAction.NavigateToExpense(it)) },
        navigateToIncome = { viewModel.submitAction(HomeUiAction.NavigateToIncome(it)) },
        navigateToBudgetDetails = { viewModel.submitAction(HomeUiAction.NavigateToBudgetDetails(it)) },
        navigateToBudgets = { viewModel.submitAction(HomeUiAction.NavigateToBudgets) },
        navigateToTransactions = { viewModel.submitAction(HomeUiAction.NavigateToTransactions) },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    uiState: UiState<HomeModel>,
    homeUiStateHolder: HomeUiState,
    modelProducer: CartesianChartModelProducer,
    setPeriod: (Instant) -> Unit,
    setDisplayTransactions: (Boolean) -> Unit,
    navigateToProfile: () -> Unit,
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit,
    navigateToBudgetDetails: (String) -> Unit,
    navigateToBudgets: () -> Unit,
    navigateToTransactions: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier
) {
    var showPeriodPicker by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            STTopAppBar(
                title = {
                    STDropdown(
                        text = formatDate(homeUiStateHolder.selectedDate),
                        onClick = { showPeriodPicker = !showPeriodPicker }
                    )
                },
                actionIcon = {
                    Icon(
                        imageVector = SpendTrackIcons.notification,
                        contentDescription = stringResource(id = R.string.notifications),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    // TODO("Add profile image and navigate to profile")
                }
            )
        },
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        // Get the bottom navigation bar insets (padding)
        val bottomPadding = innerPadding.calculateBottomPadding().minus(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
        val contentPadding = Modifier.padding(
            top = innerPadding.calculateTopPadding(),
            bottom = bottomPadding
        )
        CommonScreen(state = uiState) { homeModel ->
            HomeContent(
                homeModel = homeModel,
                homeUiStateHolder = homeUiStateHolder,
                modelProducer = modelProducer,
                setDisplayTransactions = setDisplayTransactions,
                navigateToExpense = navigateToExpense,
                navigateToIncome = navigateToIncome,
                navigateToBudgetDetails = navigateToBudgetDetails,
                navigateToBudgets = navigateToBudgets,
                navigateToTransactions = navigateToTransactions,
                modifier = contentPadding
            )
        }
    }
    if (showPeriodPicker) {
        PeriodPicker(
            selectedPeriod = homeUiStateHolder.selectedDate,
            onSelectedPeriodChanged = setPeriod,
            onDismissRequest = { showPeriodPicker = false }
        )
    }
}

@Composable
fun HomeContent(
    homeModel: HomeModel,
    homeUiStateHolder: HomeUiState,
    modelProducer: CartesianChartModelProducer,
    setDisplayTransactions: (Boolean) -> Unit,
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit,
    navigateToBudgetDetails: (String) -> Unit,
    navigateToBudgets: () -> Unit,
    navigateToTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    HomeList(
        homeModel = homeModel,
        homeUiStateHolder = homeUiStateHolder,
        modelProducer = modelProducer,
        setDisplayTransactions = setDisplayTransactions,
        navigateToExpense = navigateToExpense,
        navigateToIncome = navigateToIncome,
        navigateToBudgetDetails = navigateToBudgetDetails,
        navigateToBudgets = navigateToBudgets,
        navigateToTransactions = navigateToTransactions,
        modifier = modifier
    )
}

@Composable
fun HomeList(
    homeModel: HomeModel,
    homeUiStateHolder: HomeUiState,
    modelProducer: CartesianChartModelProducer,
    setDisplayTransactions: (Boolean) -> Unit,
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit,
    navigateToBudgetDetails: (String) -> Unit,
    navigateToBudgets: () -> Unit,
    navigateToTransactions: () -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            HomeHeader(
                accountBalance = homeModel.accountBalance,
                amountSpent = homeModel.amountSpent,
                amountEarned = homeModel.amountEarned,
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )
        }
        item {
            HomeReportChart(
                selectedMonth = homeUiStateHolder.selectedDate,
                transactions = homeModel.transactionsReport,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                modelProducer = modelProducer
            )
        }
        item {
            TransactionOrCategoryHeader(
                isTransactionViewActive = homeUiStateHolder.isTransactionViewActive,
                setDisplayTransactions = setDisplayTransactions,
                navigateToTransactions = navigateToTransactions,
                navigateToBudgets = navigateToBudgets,
            )
        }
        if (homeUiStateHolder.isTransactionViewActive) {
            if (homeModel.transactions.isEmpty()) {
                item {
                    EmptyTransaction(
                        text = stringResource(id = R.string.no_transactions),
                        iconResId = R.drawable.no_transaction
                    )
                }
            } else {
                items(homeModel.transactions.take(5), key = { it.id }) { transaction ->
                    TransactionCard(
                        category = transaction.category,
                        transactionName = transaction.name,
                        amount = transaction.amount.toInt(),
                        transactionDate = transaction.transactionDate,
                        type = if (transaction is Income) TransactionType.INCOME else TransactionType.EXPENSE,
                        onClick = {
                            if (transaction is Income) navigateToIncome(transaction.id)
                            else navigateToExpense(transaction.id)
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        } else {
            if (homeModel.budgets.isEmpty()) {
                item {
                    EmptyTransaction(
                        text = stringResource(id = R.string.no_budget_set),
                        iconResId = R.drawable.no_budget
                    )
                }
            } else {
                items(homeModel.budgets.take(3), key = { it.budgetId }) { budget ->
                    BudgetCard(
                        budget = budget,
                        onClick = {
                            navigateToBudgetDetails(budget.budgetId)
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AccountBalance(
    accountBalance: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.account_balance),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = "$${accountBalance.toInt()}",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HomeHeader(
    accountBalance: Double,
    amountSpent: Double,
    amountEarned: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AccountBalance(accountBalance = accountBalance)
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            TransactionAmountCard(
                text = stringResource(id = R.string.income),
                amount = amountEarned.toInt(),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                painter = painterResource(id = SpendTrackIcons.addIncome),
                modifier = Modifier.align(Alignment.CenterStart)
            )
            TransactionAmountCard(
                text = stringResource(id = R.string.expense),
                amount = amountSpent.toInt(),
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                painter = painterResource(id = SpendTrackIcons.addExpense),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun HomeReportChart(
    selectedMonth: Instant,
    modelProducer: CartesianChartModelProducer,
    transactions: Map<Int, Int>,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.primary

    LaunchedEffect(transactions) {
        withContext(Dispatchers.Default) {
            modelProducer.runTransaction {
                lineSeries {
                    series(x = transactions.keys.toList(), y = transactions.values.toList())
                }
            }
        }
    }

    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider =
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        remember { LineCartesianLayer.LineFill.single(fill(color)) }
                    )
                )
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis =
            HorizontalAxis.rememberBottom(
                valueFormatter = bottomAxisValueFormatter(selectedMonth),
                itemPlacer =
                remember {
                    HorizontalAxis.ItemPlacer.aligned(spacing = 3, addExtremeLabelPadding = true)
                },
            )
            ,
            layerPadding =
                cartesianLayerPadding(scalableStartPadding = 12.dp, scalableEndPadding = 12.dp),
        )
        ,
        modelProducer = modelProducer,
        modifier = modifier
    )
}

// Formatter for the day and month, e.g., "Oct 1"
private val dateFormatter = DateTimeFormatter.ofPattern("MMM d")

private fun bottomAxisValueFormatter(selectedMonth: Instant) = CartesianValueFormatter { _, x, _ ->
    val startDate = selectedMonth
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .toJavaLocalDateTime()
        .withDayOfMonth(x.toInt())

    startDate.format(dateFormatter)
}

@Composable
fun TransactionsPeriodFilterChips(
    transactionPeriodType: TransactionPeriodType,
    setFilterPeriod: (TransactionPeriodType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TransactionDateFilterChip(
            text = stringResource(id = R.string.today),
            selected = transactionPeriodType == TransactionPeriodType.DAILY,
            onClick = { setFilterPeriod(TransactionPeriodType.DAILY) }
        )
        TransactionDateFilterChip(
            text = stringResource(id = R.string.week),
            selected = transactionPeriodType == TransactionPeriodType.WEEKLY,
            onClick = { setFilterPeriod(TransactionPeriodType.WEEKLY) }
        )
        TransactionDateFilterChip(
            text = stringResource(id = R.string.month),
            selected = transactionPeriodType == TransactionPeriodType.MONTHLY,
            onClick = { setFilterPeriod(TransactionPeriodType.MONTHLY) }
        )
        TransactionDateFilterChip(
            text = stringResource(id = R.string.year),
            selected = transactionPeriodType == TransactionPeriodType.YEARLY,
            onClick = { setFilterPeriod(TransactionPeriodType.YEARLY) }
        )
    }
}

@Composable
fun TransactionOrCategoryHeader(
    isTransactionViewActive: Boolean,
    setDisplayTransactions: (Boolean) -> Unit,
    navigateToTransactions: () -> Unit,
    navigateToBudgets: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        STDropdown(
            text = stringResource(id = if (isTransactionViewActive) R.string.transactions else R.string.categories),
            onClick = { setDisplayTransactions(!isTransactionViewActive) },
            modifier = Modifier
                .align(Alignment.CenterStart)
        )
        TertiaryButton(
            text = stringResource(id = if (isTransactionViewActive) R.string.see_all else R.string.budget),
            onClick = {
                if (isTransactionViewActive) navigateToTransactions()
                else navigateToBudgets()
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
        )
    }
}

@Preview(device = "id:Nexus S", showBackground = true, showSystemUi = true)
@Composable
fun HomeHeaderPreview() {
    SpendTrackTheme {
        HomeHeader(
            accountBalance = 7200.0,
            amountSpent = 1800.0,
            amountEarned = 9000.0,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview(device = "id:Nexus S", showBackground = true, showSystemUi = true)
@Composable
fun TransactionsPeriodFilterChipsPreview() {
    SpendTrackTheme {
        TransactionsPeriodFilterChips(
            transactionPeriodType = TransactionPeriodType.MONTHLY,
            setFilterPeriod = {}
        )
    }
}

@Preview(device = "id:Nexus S", showBackground = true, showSystemUi = true)
@Composable
fun TransactionOrCategoryHeaderPreview() {
    SpendTrackTheme {
        TransactionOrCategoryHeader(
            isTransactionViewActive = false,
            setDisplayTransactions = {},
            navigateToTransactions = {},
            navigateToBudgets = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
