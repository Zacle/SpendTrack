package com.zacle.spendtrack.feature.budget.add_edit_budget

import android.annotation.SuppressLint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.noRippleEffect
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.util.getCurrencies
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.feature.budget.CreateBudgetContent

@Composable
fun AddEditBudgetRoute(
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditBudgetViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val stateHolder by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect { event ->
            when (event) {
                is AddEditBudgetUiEvent.InvalidAmountError -> {
                    snackbarHostState.showSnackbar(context.getString(event.messageResId))
                }
                is AddEditBudgetUiEvent.CategoryNotSelectedError -> {
                    snackbarHostState.showSnackbar(context.getString(event.messageResId))
                }
                is AddEditBudgetUiEvent.NavigateBack -> navigateBack()
                is AddEditBudgetUiEvent.NavigateToLogin -> navigateToLogin()
            }
        }
    }

    AddEditBudgetScreen(
        stateHolder = stateHolder,
        snackbarHostState = snackbarHostState,
        onAmountChanged = { viewModel.submitAction(AddEditBudgetUiAction.OnAmountChanged(it)) },
        onCategorySelected = { viewModel.submitAction(AddEditBudgetUiAction.OnCategorySelected(it)) },
        onBudgetAlertChanged = { viewModel.submitAction(AddEditBudgetUiAction.OnBudgetAlertChanged(it)) },
        onBudgetAlertPercentageChanged = { viewModel.submitAction(AddEditBudgetUiAction.OnBudgetAlertPercentageChanged(it)) },
        onRecurrentChanged = { viewModel.submitAction(AddEditBudgetUiAction.OnRecurrentChanged(it)) },
        onSaveBudget = { viewModel.submitAction(AddEditBudgetUiAction.OnSaveBudget) },
        onNavigateBack = navigateBack,
        modifier = modifier
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddEditBudgetScreen(
    stateHolder: AddEditBudgetUiState,
    snackbarHostState: SnackbarHostState,
    onAmountChanged: (Double) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onBudgetAlertChanged: (Boolean) -> Unit,
    onBudgetAlertPercentageChanged: (Int) -> Unit,
    onRecurrentChanged: (Boolean) -> Unit,
    onSaveBudget: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier
) {
    Scaffold(
        topBar = {
            STTopAppBar(
                title = {
                    Text(text = stringResource(
                        if (stateHolder.budgetId == null) R.string.create_budget else R.string.edit_budget
                    ))
                },
                navigationIcon = {
                    Icon(
                        imageVector = SpendTrackIcons.arrowBack,
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier.noRippleEffect { onNavigateBack() }
                    )
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
    ) {
        AddEditBudgetContent(
            stateHolder = stateHolder,
            onAmountChanged = onAmountChanged,
            onCategorySelected = onCategorySelected,
            onBudgetAlertChanged = onBudgetAlertChanged,
            onBudgetAlertPercentageChanged = onBudgetAlertPercentageChanged,
            onRecurrentChanged = onRecurrentChanged,
            onSaveBudget = onSaveBudget
        )
    }
}

@Composable
fun AddEditBudgetContent(
    stateHolder: AddEditBudgetUiState,
    onAmountChanged: (Double) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onBudgetAlertChanged: (Boolean) -> Unit,
    onBudgetAlertPercentageChanged: (Int) -> Unit,
    onRecurrentChanged: (Boolean) -> Unit,
    onSaveBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencies = getCurrencies(LocalContext.current)

    val currency = currencies.find { it.code == stateHolder.currencyCode }?.symbol ?: "$"

    CreateBudgetContent(
        amount = stateHolder.amount,
        currency = currency,
        categories = stateHolder.categories,
        selectedCategoryId = stateHolder.selectedCategory.categoryId,
        receiveAlert = stateHolder.budgetAlert,
        receiveAlertPercentage = stateHolder.budgetAlertPercentage,
        recurrent = stateHolder.recurrent,
        onAmountChanged = { onAmountChanged(it.toDouble()) },
        onCategorySelected = onCategorySelected,
        onReceiveAlertChanged = onBudgetAlertChanged,
        onReceiveAlertPercentageChanged = onBudgetAlertPercentageChanged,
        onRecurrentChanged = onRecurrentChanged,
        onSaveBudget = onSaveBudget,
        modifier = modifier
    )
}
