package com.zacle.spendtrack.feature.report

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.zacle.spendtrack.core.designsystem.component.ProgressIndicator
import com.zacle.spendtrack.core.designsystem.component.STDropdown
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.TransactionCard
import com.zacle.spendtrack.core.designsystem.component.TransactionType
import com.zacle.spendtrack.core.designsystem.component.noRippleEffect
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.designsystem.util.CategoryKeyResource
import com.zacle.spendtrack.core.domain.CategoryStats
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.CommonScreen
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.ext.formatDate
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ReportRoute(
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val stateHolder by viewModel.uiState.collectAsStateWithLifecycle()

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect { event ->
            when (event) {
                is ReportUiEvent.NavigateToExpense -> navigateToExpense(event.expenseId)
                is ReportUiEvent.NavigateToIncome -> navigateToIncome(event.incomeId)
                is ReportUiEvent.NavigateToLogin -> navigateToLogin()
            }
        }
    }

    ReportScreen(
        uiState = uiState,
        stateHolder = stateHolder,
        modelProducer = modelProducer,
        snackbarHostState = snackbarHostState,
        onReportPeriodPressed = { viewModel.submitAction(ReportUiAction.OnReportPeriodPressed) },
        onReportPeriodDismissed = { viewModel.submitAction(ReportUiAction.OnReportPeriodDismissed) },
        onReportPeriodConfirmed = { viewModel.submitAction(ReportUiAction.OnReportPeriodConfirmed(it)) },
        onReportTransactionTypeChanged = { viewModel.submitAction(ReportUiAction.OnRecordTransactionTypeChanged(it)) },
        onChartTypeChanged = { viewModel.submitAction(ReportUiAction.OnChartTypeChanged(it)) },
        onShowTransactions = { viewModel.submitAction(ReportUiAction.OnShowTransactions(it)) },
        navigateToExpense = { viewModel.submitAction(ReportUiAction.OnNavigateToExpense(it)) },
        navigateToIncome = { viewModel.submitAction(ReportUiAction.OnNavigateToIncome(it)) },
        modifier = modifier
    )
}

@Composable
internal fun ReportScreen(
    uiState: UiState<ReportModel>,
    stateHolder: ReportUiState,
    modelProducer: CartesianChartModelProducer,
    snackbarHostState: SnackbarHostState,
    onReportPeriodPressed: () -> Unit,
    onReportPeriodDismissed: () -> Unit,
    onReportPeriodConfirmed: (Instant) -> Unit,
    onChartTypeChanged: (ChartType) -> Unit,
    onReportTransactionTypeChanged: (RecordTransactionType) -> Unit,
    onShowTransactions: (Boolean) -> Unit,
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit,
    modifier: Modifier
) {
    Scaffold(
        topBar = {
            STTopAppBar(
                title = { Text(text = stringResource(R.string.report_title)) },
            )
        },
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
        ) {
            ReportHeader(
                selectedMonth = stateHolder.selectedPeriod,
                selectedChartType = stateHolder.chartType,
                shouldShowTransactions = stateHolder.shouldShowTransactions,
                onReportPeriodPressed = onReportPeriodPressed,
                onChartTypeChanged = onChartTypeChanged,
                setShouldShowTransactions = onShowTransactions,
                modifier = Modifier.padding(16.dp)
            )
            CommonScreen(uiState) { reportModel ->
                ReportContent(
                    reportModel = reportModel,
                    stateHolder = stateHolder,
                    modelProducer = modelProducer,
                    onReportTransactionTypeChanged = onReportTransactionTypeChanged,
                    navigateToExpense = navigateToExpense,
                    navigateToIncome = navigateToIncome
                )
            }
        }
    }
}

@Composable
fun ReportContent(
    reportModel: ReportModel,
    stateHolder: ReportUiState,
    modelProducer: CartesianChartModelProducer,
    onReportTransactionTypeChanged: (RecordTransactionType) -> Unit,
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (stateHolder.shouldShowTransactions) {
                TransactionReportChart(
                    selectedMonth = stateHolder.selectedPeriod,
                    modelProducer = modelProducer,
                    transactions =
                        if (stateHolder.recordTransactionType == RecordTransactionType.EXPENSE)
                            reportModel.expensesReport
                        else
                            reportModel.incomesReport,
                    chartType = stateHolder.chartType,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            } else {
                if (stateHolder.recordTransactionType == RecordTransactionType.EXPENSE)
                    CategoriesPieChart(categories = reportModel.categoryExpensesReport)
                else
                    CategoriesPieChart(categories = reportModel.categoryIncomesReport)
            }
        }
        item {
            RecordTransactionTypeRow(
                recordTransactionType = stateHolder.recordTransactionType,
                onRecordTransactionTypeChanged = onReportTransactionTypeChanged
            )
        }
        if (stateHolder.shouldShowTransactions) {
            items(
                reportModel
                    .transactions
                    .filterIsInstance(
                        if (stateHolder.recordTransactionType == RecordTransactionType.EXPENSE)
                            Expense::class.java
                        else Income::class.java
                    ), key = { it.id }
            ) { transaction ->
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
        } else {
            if (stateHolder.recordTransactionType == RecordTransactionType.EXPENSE) {
                items(reportModel.categoryExpensesReport.toList()) { (category, stats) ->
                    RecordCategoryCard(
                        name = CategoryKeyResource.getStringResourceForCategory(
                            context = LocalContext.current,
                            categoryKey = category.key
                        ),
                        color = Color(android.graphics.Color.parseColor(category.color)),
                        amount = stats.categoryAmount,
                        percentage = stats.categoryPercentage,
                        recordTransactionType = stateHolder.recordTransactionType
                    )
                }
            } else {
                items(reportModel.categoryIncomesReport.toList()) { (category, stats) ->
                    RecordCategoryCard(
                        name = CategoryKeyResource.getStringResourceForCategory(
                            context = LocalContext.current,
                            categoryKey = category.key
                        ),
                        color = Color(android.graphics.Color.parseColor(category.color)),
                        amount = stats.categoryAmount,
                        percentage = stats.categoryPercentage,
                        recordTransactionType = stateHolder.recordTransactionType
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionReportChart(
    selectedMonth: Instant,
    modelProducer: CartesianChartModelProducer,
    transactions: Map<Int, Int>,
    chartType: ChartType,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(
        key1 = transactions,
        key2 = chartType,
    ) {
        withContext(Dispatchers.Default) {
            modelProducer.runTransaction {
                if (chartType == ChartType.LINE) {
                    lineSeries {
                        series(x = transactions.keys.toList(), y = transactions.values.toList())
                    }
                } else if (chartType == ChartType.BAR) {
                    columnSeries {
                        series(x = transactions.keys.toList(), y = transactions.values.toList())
                    }
                }
            }
        }
    }

    when (chartType) {
        ChartType.LINE -> LineChart(
            selectedMonth = selectedMonth,
            modelProducer = modelProducer,
            modifier = modifier
        )
        ChartType.BAR -> ColumnChart(
            selectedMonth = selectedMonth,
            modelProducer = modelProducer,
            modifier = modifier
        )
        else -> {}
    }
    
}

@Composable
fun LineChart(
    selectedMonth: Instant,
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.primary

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

@Composable
fun ColumnChart(
    selectedMonth: Instant,
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.primary

    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            color = color,
                            thickness = 12.dp,
                            shape = CorneredShape.rounded(allPercent = 40),
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
            )
        ,
        modelProducer = modelProducer,
        modifier = modifier
    )
}

@Composable
fun CategoriesPieChart(
    categories: Map<Category, CategoryStats>,
    modifier: Modifier = Modifier
) {
    val pieData = categories.map { (category, stats) ->
        Pie(
           label = CategoryKeyResource.getStringResourceForCategory(
               context = LocalContext.current,
               categoryKey = category.key
           ),
            data = stats.categoryPercentage.toDouble(),
            color = Color(android.graphics.Color.parseColor(category.color))
        )
    }

    PieChart(
        data = pieData,
        selectedScale = 1.2f,
        scaleAnimEnterSpec = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        colorAnimEnterSpec = tween(300),
        colorAnimExitSpec = tween(300),
        scaleAnimExitSpec = tween(300),
        spaceDegreeAnimExitSpec = tween(300),
        spaceDegree = 7f,
        selectedPaddingDegree = 4f,
        style = Pie.Style.Stroke(width = 100.dp),
        modifier = modifier.size(200.dp)
    )
}

@Composable
fun ReportHeader(
    selectedMonth: Instant,
    selectedChartType: ChartType,
    shouldShowTransactions: Boolean,
    onReportPeriodPressed: () -> Unit,
    onChartTypeChanged: (ChartType) -> Unit,
    setShouldShowTransactions: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        STDropdown(
            text = formatDate(selectedMonth),
            onClick = onReportPeriodPressed,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            STDropdown(
                text = stringResource(if (shouldShowTransactions) R.string.transactions else R.string.categories),
                onClick = { setShouldShowTransactions(!shouldShowTransactions) },
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
            if (shouldShowTransactions) {
                ChartFilters(
                    selectedChartType = selectedChartType,
                    onChartTypeChanged = onChartTypeChanged,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
fun ChartFilters(
    selectedChartType: ChartType,
    onChartTypeChanged: (ChartType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .noRippleEffect {
                    onChartTypeChanged(ChartType.LINE)
                },
            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
            color = if (selectedChartType == ChartType.LINE) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface,
            contentColor = if (selectedChartType == ChartType.LINE) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        ) {
            Icon(
                painter = painterResource(id = R.drawable.line_chart),
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
            )
        }
        Surface(
            modifier = Modifier
                .noRippleEffect {
                    onChartTypeChanged(ChartType.BAR)
                },
            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
            color = if (selectedChartType == ChartType.BAR) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface,
            contentColor = if (selectedChartType == ChartType.BAR) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        ) {
            Icon(
                painter = painterResource(id = R.drawable.bar_chart),
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
            )
        }
    }
}

@Composable
fun RecordTransactionTypeRow(
    recordTransactionType: RecordTransactionType,
    onRecordTransactionTypeChanged: (RecordTransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .noRippleEffect {
                    onRecordTransactionTypeChanged(RecordTransactionType.EXPENSE)
                },
            color = if (recordTransactionType == RecordTransactionType.EXPENSE)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface,
            contentColor =
            if (recordTransactionType == RecordTransactionType.EXPENSE)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface,
            shape = CircleShape
        ) {
            Text(
                text = stringResource(R.string.expense),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            )
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .noRippleEffect {
                    onRecordTransactionTypeChanged(RecordTransactionType.INCOME)
                },
            color = if (recordTransactionType == RecordTransactionType.INCOME)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface,
            contentColor =
            if (recordTransactionType == RecordTransactionType.INCOME)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface,
            shape = CircleShape
        ) {
            Text(
                text = stringResource(R.string.income),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 10.dp)
            )
        }
    }
}

@Composable
fun RecordCategoryCard(
    name: String,
    color: Color,
    amount: Int,
    percentage: Float,
    recordTransactionType: RecordTransactionType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier,
                shape = CircleShape,
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                color = Color.Transparent
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(12.dp)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${if (recordTransactionType == RecordTransactionType.EXPENSE) "-" else "+"}$$amount",
                style = MaterialTheme.typography.bodyMedium,
                color =
                    if (recordTransactionType == RecordTransactionType.EXPENSE)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
            )
        }
        ProgressIndicator(
            progress = percentage,
            color = color,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChartFiltersPreview() {
    SpendTrackTheme {
        ReportHeader(
            selectedMonth = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
            selectedChartType = ChartType.LINE,
            shouldShowTransactions = true,
            onReportPeriodPressed = {},
            onChartTypeChanged = {},
            setShouldShowTransactions = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecordTransactionTypeRowPreview() {
    SpendTrackTheme {
        RecordTransactionTypeRow(
            recordTransactionType = RecordTransactionType.INCOME,
            onRecordTransactionTypeChanged = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecordCategoryCardPreview() {
    SpendTrackTheme {
        RecordCategoryCard(
            name = "Education",
            color = Color(0XFFFF7043),
            amount = 1000,
            percentage = 50f,
            recordTransactionType = RecordTransactionType.EXPENSE
        )
    }
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