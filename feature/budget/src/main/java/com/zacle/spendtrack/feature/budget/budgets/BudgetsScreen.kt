package com.zacle.spendtrack.feature.budget.budgets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zacle.spendtrack.core.designsystem.component.BudgetCard
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.SpendTrackButton
import com.zacle.spendtrack.core.designsystem.component.noRippleEffect
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.CommonScreen
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.feature.budget.BudgetCircularIndicator
import com.zacle.spendtrack.feature.budget.EmptyBudgetScreen
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun BudgetsRoute(
    navigateToBudgetDetails: (String) -> Unit,
    navigateToCreateBudget: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BudgetsViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val stateHolder by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect { event ->
            when (event) {
                is BudgetsUiEvent.NavigateToLogin -> navigateToLogin()
                is BudgetsUiEvent.NavigateToCreateBudget -> navigateToCreateBudget()
                is BudgetsUiEvent.NavigateToBudgetDetails -> navigateToBudgetDetails(event.budgetId)
            }
        }
    }

    BudgetsScreen(
        uiState = uiState,
        stateHolder = stateHolder,
        onNextMonthPressed = { viewModel.submitAction(BudgetsUiAction.OnNextMonthPressed) },
        onPreviousMonthPressed = { viewModel.submitAction(BudgetsUiAction.OnPreviousMonthPressed) },
        onCreateBudgetPressed = { viewModel.submitSingleEvent(BudgetsUiEvent.NavigateToCreateBudget) },
        onBudgetPressed = { viewModel.submitSingleEvent(BudgetsUiEvent.NavigateToBudgetDetails(it)) },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun BudgetsScreen(
    uiState: UiState<BudgetsModel>,
    stateHolder: BudgetsUiState,
    onNextMonthPressed: () -> Unit,
    onPreviousMonthPressed: () -> Unit,
    onCreateBudgetPressed: () -> Unit,
    onBudgetPressed: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier
) {
    val currentMonth = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).month
    val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year

    Scaffold(
        topBar = {
            STTopAppBar(
                title = {
                    Text(
                        text = stateHolder
                            .selectedMonth
                            .month.
                            getDisplayName(TextStyle.FULL, Locale.getDefault())
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = null,
                        modifier = Modifier.noRippleEffect { onPreviousMonthPressed() }
                    )
                },
                actionIcon = {
                    if (stateHolder.selectedMonth.month < currentMonth ||
                        stateHolder.selectedMonth.year < currentYear) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .noRippleEffect { onNextMonthPressed() }
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) { innerPadding ->
        CommonScreen(uiState) { budgetsModel ->
            if (budgetsModel.budgets.isEmpty()) {
                EmptyBudgetScreen(
                    onCreateBudget = onCreateBudgetPressed,
                    enabled = stateHolder.selectedMonth.month == currentMonth &&
                            stateHolder.selectedMonth.year == currentYear,
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                )
            } else {
                BudgetsContent(
                    budgetsModel = budgetsModel,
                    onCreateBudgetPressed = onCreateBudgetPressed,
                    onBudgetPressed = onBudgetPressed,
                    isCurrentMonth = stateHolder.selectedMonth.month == currentMonth &&
                            stateHolder.selectedMonth.year == currentYear,
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                )
            }
        }
    }
}

@Composable
fun BudgetsContent(
    budgetsModel: BudgetsModel,
    isCurrentMonth: Boolean,
    onCreateBudgetPressed: () -> Unit,
    onBudgetPressed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 4.dp
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            item {
                BudgetsContentHeader(budgetsModel = budgetsModel)
            }
            items(budgetsModel.budgets, key = { it.budgetId }) { budget ->
                BudgetCard(
                    budget = budget,
                    onClick = { onBudgetPressed(budget.budgetId) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            if (isCurrentMonth) {
                item {
                    SpendTrackButton(
                        text = stringResource(R.string.create_budget),
                        onClick = onCreateBudgetPressed,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetsContentHeader(
    budgetsModel: BudgetsModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BudgetCircularIndicator(
            totalAmount = budgetsModel.totalBudget.toFloat(),
            remainingAmount = budgetsModel.remainingBudget.toFloat(),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(R.string.planned_budget)
                    + " $${budgetsModel.totalBudget.toInt()} " +
                    stringResource(R.string.with) +
                    " $${budgetsModel.remainingBudget.toInt()} "
                    + stringResource(R.string.budget_left),
            fontWeight = FontWeight.Medium,
            modifier = modifier
                .weight(1f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BudgetsContentHeaderPreview() {
    SpendTrackTheme {
        BudgetsContentHeader(
            budgetsModel = BudgetsModel(totalBudget = 3200.0, remainingBudget = 1200.0, budgets = emptyList())
        )
    }
}
