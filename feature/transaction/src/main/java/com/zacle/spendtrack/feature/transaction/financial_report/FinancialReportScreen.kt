package com.zacle.spendtrack.feature.transaction.financial_report

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zacle.spendtrack.core.designsystem.component.STOutline
import com.zacle.spendtrack.core.designsystem.component.noRippleEffect
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.designsystem.util.CategoryKeyResource
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.CommonScreen
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.composition_local.LocalCurrency
import kotlinx.datetime.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun FinancialReportRoute(
    navigateUp: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FinancialReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(initialPage = currentPage) { 3 }

    LaunchedEffect(currentPage) {
        pagerState.animateScrollToPage(
            page = currentPage,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect {
            when(it) {
                is FinancialReportUiEvent.NavigateToLogin -> navigateToLogin()
                is FinancialReportUiEvent.NavigateBack -> navigateUp()
            }
        }
    }

    val backgroundColor =
        when (currentPage) {
            0 -> Color(0xFFEA6830)
            1 -> MaterialTheme.colorScheme.tertiaryContainer
            else -> MaterialTheme.colorScheme.primary
        }
    val contentColor =
        when (currentPage) {
            0 -> MaterialTheme.colorScheme.onError
            1 -> MaterialTheme.colorScheme.onTertiaryContainer
            else -> MaterialTheme.colorScheme.onPrimary
        }

    FinancialReportScreen(
        state = uiState,
        pagerState = pagerState,
        progress = progress,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        financialReportDate = viewModel.getFinancialReportDate(),
        onBackCLick = navigateUp,
        modifier = modifier
    )
}

@Composable
fun FinancialReportScreen(
    state: UiState<FinancialReportModel>,
    pagerState: PagerState,
    progress: Float,
    backgroundColor: Color,
    contentColor: Color,
    financialReportDate: LocalDateTime,
    onBackCLick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CommonScreen(state) { financialReportModel ->
        Surface(
            modifier = modifier
                .fillMaxSize(),
            color = backgroundColor
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                ) { index ->
                    PagerScreen(
                        financialReportModel = financialReportModel,
                        pagerIndex = index
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Indicators(
                        size = pagerState.pageCount,
                        currentPage = pagerState.currentPage,
                        currentProgress = progress,
                        onBackCLick = onBackCLick
                    )
                    val displayMonth = financialReportDate
                        .month
                        .getDisplayName(TextStyle.FULL, Locale.getDefault())
                    Text(
                        text = stringResource(R.string.month) + " : $displayMonth, ${financialReportDate.year}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )

                }
            }
        }
    }
}

@Composable
fun PagerScreen(
    financialReportModel: FinancialReportModel,
    pagerIndex: Int
) {
    when (pagerIndex) {
        0 -> {
            if (financialReportModel.biggestExpense == null) {
                EmptyFinancialReport(text = stringResource(R.string.no_spending))
            } else {
                TransactionFinancialReportContent(
                    bodyText = stringResource(R.string.amount_spent),
                    amount = financialReportModel.biggestExpense.amount.toInt(),
                    highlightText = stringResource(R.string.biggest_spending),
                    category = financialReportModel.biggestExpense.category
                )
            }
        }
        1 -> {
            if (financialReportModel.biggestIncome == null) {
                EmptyFinancialReport(text = stringResource(R.string.no_earning))
            } else {
                TransactionFinancialReportContent(
                    bodyText = stringResource(R.string.amount_earned),
                    amount = financialReportModel.biggestIncome.amount.toInt(),
                    highlightText = stringResource(R.string.biggest_income),
                    category = financialReportModel.biggestIncome.category
                )
            }
        }
        2 -> {
            if (financialReportModel.budgetsSize == 0) {
                EmptyFinancialReport(text = stringResource(R.string.no_budget_set))
            } else {
                BudgetFinancialReport(
                    budgetsSize = financialReportModel.budgetsSize,
                    exceedingBudgets = financialReportModel.exceedingBudgets
                )
            }
        }
        else -> {}
    }
}

@Composable
fun TransactionFinancialReportContent(
    bodyText: String,
    amount: Int,
    highlightText: String,
    category: Category,
    modifier: Modifier = Modifier
) {
    val currency = LocalCurrency.current
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Text(
                text = bodyText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$amount$currency",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        FinancialReportHighlight(
            amount = amount,
            text = highlightText,
            category = category,
            currency = currency,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun BudgetFinancialReport(
    budgetsSize: Int,
    exceedingBudgets: List<Budget>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text =
                    if (exceedingBudgets.isEmpty()) stringResource(R.string.no_budget_exceeds)
                    else "${exceedingBudgets.size} " + stringResource(R.string.of) + " $budgetsSize " +
                            stringResource(R.string.budget_exceeds),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
            items(exceedingBudgets) { budget ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        val category = budget.category
                        val color = Color(android.graphics.Color.parseColor(category.color))
                        Surface(
                            color = color.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp),
                            tonalElevation = 5.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(
                                        CategoryKeyResource.getIconResourceForCategory(category.key)
                                    ),
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier
                                        .size(24.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = CategoryKeyResource.getStringResourceForCategory(
                                context = LocalContext.current,
                                categoryKey = category.key
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyFinancialReport(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun Indicators(
    size: Int,
    currentPage: Int,
    currentProgress: Float,
    onBackCLick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = SpendTrackIcons.arrowBack,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(end = 4.dp)
                .noRippleEffect { onBackCLick() }
        )
        repeat(size) {
            Indicator(
                isSelected = it == currentPage,
                progress = currentProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
fun Indicator(
    isSelected: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val progressColor = MaterialTheme.colorScheme.primaryContainer
    val backgroundColor = Color.LightGray
    Canvas(
        modifier = modifier
            .padding(top = 4.dp)
            .height(4.dp)
    ) {
        drawLine(
            color = backgroundColor,
            strokeWidth = size.height,
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = this.size.width, y = 0f),
            cap = StrokeCap.Round
        )
        if (isSelected) {
            drawLine(
                color = progressColor,
                strokeWidth = size.height,
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = this.size.width * progress, y = 0f),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun FinancialReportHighlight(
    amount: Int,
    text: String,
    currency: String,
    category: Category,
    modifier: Modifier = Modifier
) {
    val color = Color(android.graphics.Color.parseColor(category.color))
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 5.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            STOutline(
                modifier = Modifier
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Surface(
                        color = color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp),
                        tonalElevation = 5.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(
                                    CategoryKeyResource.getIconResourceForCategory(category.key)
                                ),
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = CategoryKeyResource.getStringResourceForCategory(
                            context = LocalContext.current,
                            categoryKey = category.key
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = "$amount$currency",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun IndicatorsPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        Indicators(
            size = 3,
            currentPage = 1,
            currentProgress = 0.5f,
            onBackCLick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransactionFinancialReportContentPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        TransactionFinancialReportContent(
            bodyText = stringResource(R.string.amount_spent),
            amount = 999,
            highlightText = stringResource(R.string.biggest_spending),
            category = Category(
                categoryId = "1",
                key = "education",
                color = "#e4b38a",
                icon = R.drawable.education
            )
        )
    }
}