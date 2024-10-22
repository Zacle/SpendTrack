package com.zacle.spendtrack.feature.budget.view_budget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zacle.spendtrack.core.designsystem.component.ProgressIndicator
import com.zacle.spendtrack.core.designsystem.component.RemoveTransactionModalSheet
import com.zacle.spendtrack.core.designsystem.component.STOutline
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.SpendTrackButton
import com.zacle.spendtrack.core.designsystem.component.TransactionCard
import com.zacle.spendtrack.core.designsystem.component.TransactionType
import com.zacle.spendtrack.core.designsystem.component.noRippleEffect
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.designsystem.util.CategoryKeyResource
import com.zacle.spendtrack.core.designsystem.util.getCurrencies
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.Loading
import com.zacle.spendtrack.core.ui.UiState
import android.graphics.Color as AndroidColor

@Composable
fun BudgetDetailRoute(
    onBackClick: () -> Unit,
    onEditBudgetClick: (String) -> Unit,
    onExpenseClick: (String) -> Unit,
    onIncomeClick: (String) -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BudgetDetailViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val stateHolder by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect { event ->
            when (event) {
                is BudgetDetailUiEvent.NavigateToLogin -> navigateToLogin()
                is BudgetDetailUiEvent.NavigateToEditBudget -> onEditBudgetClick(event.budgetId)
                is BudgetDetailUiEvent.NavigateToExpenseDetail -> onExpenseClick(event.expenseId)
                is BudgetDetailUiEvent.NavigateToIncomeDetail -> onIncomeClick(event.incomeId)
                is BudgetDetailUiEvent.NavigateBack -> onBackClick()
            }
        }
    }

    BudgetDetailScreen(
        uiState = uiState,
        stateHolder = stateHolder,
        snackbarHostState = snackbarHostState,
        onEditBudgetClick = { viewModel.submitAction(BudgetDetailUiAction.OnEditPressed(it)) },
        onDeletePressed = { viewModel.submitAction(BudgetDetailUiAction.OnDeletePressed) },
        onDeleteDismissed = { viewModel.submitAction(BudgetDetailUiAction.OnDeleteDismissed) },
        onDeleteConfirmed = { viewModel.submitAction(BudgetDetailUiAction.OnDeleteConfirmed(it)) },
        onExpenseClicked = { viewModel.submitAction(BudgetDetailUiAction.OnExpenseClicked(it)) },
        onIncomeClicked = { viewModel.submitAction(BudgetDetailUiAction.OnIncomeClicked(it)) },
        onNavigateBack = onBackClick,
        modifier = modifier
    )
}

@Composable
internal fun BudgetDetailScreen(
    uiState: UiState<BudgetDetailModel>,
    stateHolder: BudgetDetailUiState,
    snackbarHostState: SnackbarHostState,
    onEditBudgetClick: (String) -> Unit,
    onDeletePressed: () -> Unit,
    onDeleteDismissed: () -> Unit,
    onDeleteConfirmed: (Budget) -> Unit,
    onExpenseClicked: (String) -> Unit,
    onIncomeClicked: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            STTopAppBar(
                title = { Text(text = stringResource(R.string.detail_budget)) },
                navigationIcon = {
                    Icon(
                        imageVector = SpendTrackIcons.arrowBack,
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier.noRippleEffect { onNavigateBack() },
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                actionIcon = {
                    Icon(
                        painter = painterResource(R.drawable.trash),
                        contentDescription = stringResource(R.string.delete_budget),
                        modifier = Modifier
                            .size(24.dp)
                            .noRippleEffect { onDeletePressed() },
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) { innerPadding ->
        when (uiState) {
            is UiState.Loading -> Loading()
            is UiState.Error -> {}
            is UiState.Success -> {
                val budgetDetailModel = uiState.data
                Box(
                    modifier = Modifier
                        .padding(top = innerPadding.calculateTopPadding())
                        .fillMaxSize()
                ) {
                    BudgetDetailContent(
                        budgetDetailModel = budgetDetailModel,
                        stateHolder = stateHolder,
                        onDeleteDismissed = onDeleteDismissed,
                        onDeleteConfirmed = onDeleteConfirmed,
                        onExpenseClicked = onExpenseClicked,
                        onIncomeClicked = onIncomeClicked
                    )
                    SpendTrackButton(
                        text = stringResource(R.string.edit),
                        onClick = { onEditBudgetClick(budgetDetailModel.budget.budgetId) },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetDetailContent(
    budgetDetailModel: BudgetDetailModel,
    stateHolder: BudgetDetailUiState,
    onDeleteDismissed: () -> Unit,
    onDeleteConfirmed: (Budget) -> Unit,
    onExpenseClicked: (String) -> Unit,
    onIncomeClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencies = getCurrencies(LocalContext.current)
    val currencySymbol = currencies.find { it.code == stateHolder.currencyCode }?.symbol ?: "$"

    var showTransactions by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        item {
            BudgetDetailHeader(
                budget = budgetDetailModel.budget,
                currencySymbol = currencySymbol
            )
        }
        item {
            DetailBudgetOverviewTitle(
                showTransactions = showTransactions,
                onShowTransactions = { showTransactions = !showTransactions },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        if (showTransactions) {
            items(budgetDetailModel.transactions, key = { it.id }) { transaction ->
                TransactionCard(
                    category = transaction.category,
                    currencySymbol = currencySymbol,
                    transactionName = transaction.name,
                    amount = transaction.amount.toInt(),
                    transactionDate = transaction.transactionDate,
                    type = if (transaction is Income) TransactionType.INCOME else TransactionType.EXPENSE,
                    onClick = {
                        if (transaction is Income) onIncomeClicked(transaction.id)
                        else onExpenseClicked(transaction.id)
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
    if (stateHolder.shouldDisplayRemoveBudgetDialog) {
        RemoveTransactionModalSheet(
            title = stringResource(R.string.delete_budget),
            description = stringResource(R.string.delete_budget_description),
            onDismiss = onDeleteDismissed,
            onConfirm = { onDeleteConfirmed(budgetDetailModel.budget) }
        )
    }
}

@Composable
fun BudgetDetailHeader(
    budget: Budget,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val color = Color(AndroidColor.parseColor(budget.category.color))
    val isBudgetExceeded = budget.remainingAmount <= 0
    val progress =
        if (isBudgetExceeded) 1f
        else (budget.amount - budget.remainingAmount).toFloat() / budget.amount.toFloat()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        CategoryName(category = budget.category, color = color)
        Text(
            text = stringResource(R.string.remaining),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${if (isBudgetExceeded) 0 else budget.remainingAmount.toInt()}$currencySymbol",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        ProgressIndicator(
            progress = progress,
            color = color,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
        )
        if (isBudgetExceeded) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = SpendTrackIcons.warning),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier
                            .size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.exceeded_limit),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CategoryName(
    category: Category,
    color: Color,
    modifier: Modifier = Modifier
) {
    STOutline(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp),
                tonalElevation = 5.dp
            ) {
                Box(
                    modifier = Modifier
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            CategoryKeyResource.getIconResourceForCategory(category.key)
                        ),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier
                            .size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = CategoryKeyResource.getStringResourceForCategory(
                    context = LocalContext.current,
                    categoryKey = category.key
                ),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun DetailBudgetOverviewTitle(
    showTransactions: Boolean,
    onShowTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationValue = animateFloatAsState(
        label = "animateDropDown",
        targetValue = if (showTransactions) 180f else 0f,
        animationSpec = tween(300)
    )

    STOutline(
        modifier = modifier
            .noRippleEffect {
                onShowTransactions()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.overview),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .weight(1f)
            )
            Icon(
                imageVector = SpendTrackIcons.dropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .rotate(rotationValue.value)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BudgetDetailHeardPreview() {
    SpendTrackTheme {
        BudgetDetailHeader(
            budget = Budget(
                category = Category(
                    categoryId = "1",
                    key = "health_fitness",
                    icon = R.drawable.health_fitness,
                    color = "#FF7043"
                ),
                amount = 1000.0,
                remainingAmount = -500.0
            ),
            currencySymbol = "ƒ"
        )
    }
}