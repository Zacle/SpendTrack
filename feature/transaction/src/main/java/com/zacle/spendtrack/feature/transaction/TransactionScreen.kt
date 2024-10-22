package com.zacle.spendtrack.feature.transaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zacle.spendtrack.core.designsystem.component.EmptyTransaction
import com.zacle.spendtrack.core.designsystem.component.PeriodPicker
import com.zacle.spendtrack.core.designsystem.component.STDropdown
import com.zacle.spendtrack.core.designsystem.component.STOutline
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.SpendTrackButton
import com.zacle.spendtrack.core.designsystem.component.TransactionCard
import com.zacle.spendtrack.core.designsystem.component.TransactionType
import com.zacle.spendtrack.core.designsystem.component.noRippleEffect
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.designsystem.util.CategoryKeyResource
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.util.FilterState
import com.zacle.spendtrack.core.model.util.SortOrder
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.CommonScreen
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.composition_local.LocalCurrency
import com.zacle.spendtrack.core.ui.ext.formatDate
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import android.graphics.Color as AndroidColor

@Composable
fun TransactionRoute(
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit,
    navigateToFinancialReport: (Int, Int) -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val stateHolder by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect { event ->
            when (event) {
                is TransactionUiEvent.NavigateToExpense -> navigateToExpense(event.expenseId)
                is TransactionUiEvent.NavigateToIncome -> navigateToIncome(event.incomeId)
                is TransactionUiEvent.NavigateToFinancialReport ->
                    navigateToFinancialReport(event.month, event.year)
                is TransactionUiEvent.NavigateToLogin -> navigateToLogin()
            }
        }
    }

    TransactionScreen(
        uiState = uiState,
        stateHolder = stateHolder,
        snackbarHostState = snackbarHostState,
        onFilterPressed = { viewModel.submitAction(TransactionUiAction.OnFilterTransactionPressed) },
        onFilterDismissed = { viewModel.submitAction(TransactionUiAction.OnFilterTransactionDismissed) },
        onFilterApplied = { viewModel.submitAction(TransactionUiAction.OnFilterTransactionApplied(it)) },
        onMonthPeriodPressed = { viewModel.submitAction(TransactionUiAction.OnMonthPeriodPressed) },
        onMonthPeriodDismissed = { viewModel.submitAction(TransactionUiAction.OnMonthPeriodDismissed) },
        onMonthPeriodApplied = { viewModel.submitAction(TransactionUiAction.OnMonthPeriodApplied(it)) },
        navigateToExpense = { viewModel.submitAction(TransactionUiAction.OnNavigateToExpense(it)) },
        navigateToIncome = { viewModel.submitAction(TransactionUiAction.OnNavigateToIncome(it)) },
        navigateToFinancialReport = { month, year ->
            viewModel.submitAction(TransactionUiAction.OnNavigateToFinancialReport(month, year))
        },
        modifier = modifier
    )
}

@Composable
internal fun TransactionScreen(
    uiState: UiState<TransactionModel>,
    stateHolder: TransactionUiState,
    snackbarHostState: SnackbarHostState,
    onFilterPressed: () -> Unit,
    onFilterDismissed: () -> Unit,
    onFilterApplied: (TransactionUiState) -> Unit,
    onMonthPeriodPressed: () -> Unit,
    onMonthPeriodDismissed: () -> Unit,
    onMonthPeriodApplied: (Instant) -> Unit,
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit,
    navigateToFinancialReport: (Int, Int) -> Unit,
    modifier: Modifier
) {
    Scaffold(
        topBar = {
            STTopAppBar(
                title = {},
                actionIcon = {
                    TransactionFilterIcon(
                        numberOfTransactionsSelected = stateHolder.numberOfTransactionsSelected,
                        onFilterPressed = onFilterPressed
                    )
                },
                navigationIcon = {
                    STDropdown(
                        text = formatDate(stateHolder.selectedMonth),
                        onClick = { onMonthPeriodPressed() }
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) { innerPadding ->
        // Get the bottom navigation bar insets (padding)
        val bottomPadding = innerPadding.calculateBottomPadding().minus(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
        val contentPadding = Modifier.padding(
            top = innerPadding.calculateTopPadding(),
            bottom = bottomPadding
        )

        CommonScreen(uiState) { transactionModel ->
            TransactionContent(
                transactionModel = transactionModel,
                stateHolder = stateHolder,
                onFilterDismissed = onFilterDismissed,
                onFilterApplied = onFilterApplied,
                navigateToExpense = navigateToExpense,
                navigateToIncome = navigateToIncome,
                navigateToFinancialReport = navigateToFinancialReport,
                modifier = contentPadding
            )
        }
    }

    if (stateHolder.shouldDisplayMonthPeriod) {
        PeriodPicker(
            selectedPeriod = stateHolder.selectedMonth,
            onSelectedPeriodChanged = onMonthPeriodApplied,
            onDismissRequest = onMonthPeriodDismissed
        )
    }
}

@Composable
fun TransactionContent(
    transactionModel: TransactionModel,
    stateHolder: TransactionUiState,
    onFilterDismissed: () -> Unit,
    onFilterApplied: (TransactionUiState) -> Unit,
    navigateToExpense: (String) -> Unit,
    navigateToIncome: (String) -> Unit,
    navigateToFinancialReport: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            FinancialReport(
                selectedMonth = stateHolder.selectedMonth,
                onNavigateToFinancialReport = navigateToFinancialReport,
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )
        }
        if (transactionModel.transactions.isEmpty()) {
            item {
                EmptyTransaction(
                    text = stringResource(id = R.string.no_transactions),
                    iconResId = R.drawable.no_transaction,
                    modifier = Modifier
                        .padding(top = 24.dp)
                )
            }
        } else {
            items(transactionModel.transactions, key = { it.id }) { transaction ->
                TransactionCard(
                    category = transaction.category,
                    transactionName = transaction.name,
                    currencySymbol = LocalCurrency.current,
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
    }
    if (stateHolder.shouldDisplayFilterTransaction) {
        TransactionFilterModalSheet(
            categories = stateHolder.categories,
            transactionFilterUiState = stateHolder,
            onTransactionFilterUiStateChange = onFilterApplied,
            onDismiss = onFilterDismissed
        )
    }
}

@Composable
internal fun FinancialReport(
    selectedMonth: Instant,
    onNavigateToFinancialReport: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateTime = selectedMonth.toLocalDateTime(TimeZone.currentSystemDefault())

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        modifier = modifier
            .clickable {
                onNavigateToFinancialReport(dateTime.monthNumber, dateTime.year)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.financial_report),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun TransactionFilterIcon(
    numberOfTransactionsSelected: Int,
    onFilterPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .noRippleEffect { onFilterPressed() },
        contentAlignment = Alignment.Center
    ) {
        STOutline {
            Icon(
                painter = painterResource(id = SpendTrackIcons.filter),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Center)
            )
        }
        if (numberOfTransactionsSelected > 0) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = numberOfTransactionsSelected.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterModalSheet(
    categories: List<Category>,
    transactionFilterUiState: TransactionUiState,
    onTransactionFilterUiStateChange: (TransactionUiState) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember {
        mutableStateOf(transactionFilterUiState)
    }

    var showSelectCategoriesModalSheet by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.filter_transaction),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Surface(
                        shape = CircleShape,
                        color =
                            if (selectedFilter.numberOfTransactionsSelected > 0)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        contentColor =
                            if (selectedFilter.numberOfTransactionsSelected > 0)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
                        modifier = Modifier
                            .noRippleEffect {
                                onTransactionFilterUiStateChange(
                                    selectedFilter.copy(
                                        numberOfTransactionsSelected = 0,
                                        filterState = FilterState(),
                                        sortOrder = SortOrder.NEWEST
                                    )
                                )
                            }
                    ) {
                        Text(
                            text = stringResource(R.string.reset),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.filter_by),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .noRippleEffect {
                                    selectedFilter = selectedFilter.copy(
                                        filterState =
                                            selectedFilter.filterState.copy(
                                                includeIncomes = true,
                                                includeExpenses = false
                                            )
                                    )
                                },
                            shape = CircleShape,
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                            color =
                                if (selectedFilter.filterState.includeIncomes && !selectedFilter.filterState.includeExpenses)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface,
                            contentColor =
                                if (selectedFilter.filterState.includeIncomes && !selectedFilter.filterState.includeExpenses)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = stringResource(R.string.income),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .noRippleEffect {
                                    selectedFilter = selectedFilter.copy(
                                        filterState =
                                            selectedFilter.filterState.copy(
                                                includeExpenses = true,
                                                includeIncomes = false
                                            )
                                    )
                                },
                            shape = CircleShape,
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                            color =
                                if (selectedFilter.filterState.includeExpenses && !selectedFilter.filterState.includeIncomes)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface,
                            contentColor =
                                if (selectedFilter.filterState.includeExpenses && !selectedFilter.filterState.includeIncomes)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = stringResource(R.string.expense),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sort_by),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .noRippleEffect {
                                    selectedFilter = selectedFilter.copy(
                                        sortOrder = SortOrder.HIGHEST
                                    )
                                },
                            shape = CircleShape,
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                            color =
                                if (selectedFilter.sortOrder == SortOrder.HIGHEST)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface,
                            contentColor =
                                if (selectedFilter.sortOrder == SortOrder.HIGHEST)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = stringResource(R.string.Highest),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .noRippleEffect {
                                    selectedFilter = selectedFilter.copy(
                                        sortOrder = SortOrder.LOWEST
                                    )
                                },
                            shape = CircleShape,
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                            color =
                                if (selectedFilter.sortOrder == SortOrder.LOWEST)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface,
                            contentColor =
                                if (selectedFilter.sortOrder == SortOrder.LOWEST)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = stringResource(R.string.Lowest),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .noRippleEffect {
                                    selectedFilter = selectedFilter.copy(
                                        sortOrder = SortOrder.NEWEST
                                    )
                                },
                            shape = CircleShape,
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                            color =
                                if (selectedFilter.sortOrder == SortOrder.NEWEST)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface,
                            contentColor =
                                if (selectedFilter.sortOrder == SortOrder.NEWEST)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = stringResource(R.string.Newest),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .noRippleEffect {
                                    selectedFilter = selectedFilter.copy(
                                        sortOrder = SortOrder.OLDEST
                                    )
                                },
                            shape = CircleShape,
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                            color =
                                if (selectedFilter.sortOrder == SortOrder.OLDEST)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface,
                            contentColor =
                                if (selectedFilter.sortOrder == SortOrder.OLDEST)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = stringResource(R.string.Oldest),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Column {
                    Text(
                        text = stringResource(R.string.category),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .noRippleEffect {
                                showSelectCategoriesModalSheet = true
                            }
                    ) {
                        Text(
                            text = stringResource(R.string.choose_category),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "${selectedFilter.filterState.categoryIds.size} " + stringResource(R.string.selected),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                SpendTrackButton(
                    text = stringResource(R.string.apply),
                    onClick = {
                        onTransactionFilterUiStateChange(selectedFilter)
                    }
                )
            }
        }
    }

    if (showSelectCategoriesModalSheet) {
        SelectCategories(
            categories = categories,
            selectedCategoryIds = transactionFilterUiState.filterState.categoryIds.toList(),
            onDismiss = { showSelectCategoriesModalSheet = false },
            onCategoriesSelected = {
                selectedFilter = selectedFilter.copy(
                    filterState =
                        selectedFilter.filterState.copy(categoryIds = it.toSet())
                )
                showSelectCategoriesModalSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCategories(
    categories: List<Category>,
    selectedCategoryIds: List<String>,
    onCategoriesSelected: (List<String>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSelectedCategories by remember(selectedCategoryIds) {
        mutableStateOf(selectedCategoryIds)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.choose_category),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
            }
            items(categories) { category ->
                CategoryCheckBox(
                    category = category,
                    isChecked = currentSelectedCategories.contains(category.categoryId),
                    onCheckedChange = {
                        currentSelectedCategories = if (it) currentSelectedCategories.toMutableList().apply { add(category.categoryId) }
                        else currentSelectedCategories.toMutableList().apply { remove(category.categoryId) }
                    },
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                )
            }
            item {
                SpendTrackButton(
                    text = stringResource(id = R.string.done),
                    onClick = { onCategoriesSelected(currentSelectedCategories) },
                    modifier = Modifier
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryCheckBox(
    category: Category,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val color = Color(AndroidColor.parseColor(category.color))
    Surface(
        modifier = modifier,
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = 12.dp)
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = color.copy(alpha = 0.2f),
                    checkmarkColor = color,
                    uncheckedColor = color
                )
            )
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
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransactionFilterPreview() {
    SpendTrackTheme {
        TransactionFilterIcon(numberOfTransactionsSelected = 1, onFilterPressed = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CategoryCheckBoxPreview() {
    SpendTrackTheme {
        CategoryCheckBox(
            category = Category(
                categoryId = "1",
                key = "transportation",
                icon = R.drawable.transportation,
                color = "#FF7043"
            ),
            isChecked = true,
            onCheckedChange = {}
        )
    }
}