package com.zacle.spendtrack.feature.expense.add_edit_expense

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zacle.spendtrack.core.designsystem.component.RecordTransaction
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.noRippleEffect
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.ui.Loading
import com.zacle.spendtrack.core.ui.transaction.TransactionUiAction
import com.zacle.spendtrack.core.ui.transaction.TransactionUiEvent
import com.zacle.spendtrack.core.ui.transaction.TransactionUiState
import kotlinx.datetime.Instant
import com.zacle.spendtrack.core.shared_resources.R as SharedR

@Composable
fun AddEditExpenseRoute(
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditExpenseViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val stateHolder by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect { event ->
            when (event) {
                is TransactionUiEvent.BlankNameError -> {
                    showSnackbar(snackbarHostState, context.getString(event.messageResId))
                }
                is TransactionUiEvent.InvalidNameError -> {
                    showSnackbar(snackbarHostState, context.getString(event.messageResId))
                }
                is TransactionUiEvent.ShortNameError -> {
                    showSnackbar(snackbarHostState, context.getString(event.messageResId))
                }
                is TransactionUiEvent.InvalidAmountError -> {
                    showSnackbar(snackbarHostState, context.getString(event.messageResId))
                }
                is TransactionUiEvent.CategoryNotSelectedError -> {
                    showSnackbar(snackbarHostState, context.getString(event.messageResId))
                }
                is TransactionUiEvent.NavigateBack -> navigateBack()
                is TransactionUiEvent.NavigateToLogin -> navigateToLogin()
            }
        }
    }

    AddEditExpenseScreen(
        modifier = modifier,
        stateHolder = stateHolder,
        onNameChanged = { viewModel.submitAction(TransactionUiAction.OnNameChanged(it)) },
        onDescriptionChanged = { viewModel.submitAction(TransactionUiAction.OnDescriptionChanged(it)) },
        onAmountChanged = { viewModel.submitAction(TransactionUiAction.OnAmountChanged(it)) },
        onCategorySelected = { viewModel.submitAction(TransactionUiAction.OnCategorySelected(it)) },
        onDateSelected = { viewModel.submitAction(TransactionUiAction.OnDateSelected(it)) },
        onAttachmentSelected = { viewModel.submitAction(TransactionUiAction.OnAttachmentSelected(it)) },
        onExpenseSaved = { viewModel.submitAction(TransactionUiAction.OnSaveTransaction) },
        onNavigateBack = navigateBack,
        snackbarHostState = snackbarHostState
    )
}

internal suspend fun showSnackbar(snackbarHostState: SnackbarHostState, message: String) {
    snackbarHostState.showSnackbar(message = message)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddEditExpenseScreen(
    stateHolder: TransactionUiState,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onAmountChanged: (Double) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onAttachmentSelected: (ImageData?) -> Unit,
    onDateSelected: (Instant) -> Unit,
    onExpenseSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            STTopAppBar(
                title = { Text(text = stringResource(SharedR.string.expense)) },
                navigationIcon = {
                    Icon(
                        imageVector = SpendTrackIcons.arrowBack,
                        contentDescription = stringResource(SharedR.string.back),
                        modifier = Modifier.noRippleEffect { onNavigateBack() }
                    )
                },
                containerColor = Color(0xFFEA6830),
                titleContentColor = MaterialTheme.colorScheme.onErrorContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onErrorContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        },
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFEA6830),
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    ) {
        if (stateHolder.isLoading) {
            Loading()
        }
        else {
            AddEditExpenseContent(
                stateHolder = stateHolder,
                onNameChanged = onNameChanged,
                onDescriptionChanged = onDescriptionChanged,
                onAmountChanged = onAmountChanged,
                onCategorySelected = onCategorySelected,
                onAttachmentSelected = onAttachmentSelected,
                onDateSelected = onDateSelected,
                onExpenseSaved = onExpenseSaved,
            )
        }
    }
}

@Composable
fun AddEditExpenseContent(
    stateHolder: TransactionUiState,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onAmountChanged: (Double) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onAttachmentSelected: (ImageData?) -> Unit,
    onDateSelected: (Instant) -> Unit,
    onExpenseSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    RecordTransaction(
        name = stateHolder.name,
        description = stateHolder.description,
        amount = stateHolder.amount,
        categories = stateHolder.categories,
        selectedCategoryId = stateHolder.selectedCategory.categoryId,
        transactionDate = stateHolder.transactionDate,
        receiptUriImage = stateHolder.receiptImage,
        onNameChanged = onNameChanged,
        onDescriptionChanged = onDescriptionChanged,
        onAmountChanged = { onAmountChanged(it.toDouble()) },
        onCategorySelected = onCategorySelected,
        onDateSelected = onDateSelected,
        onAttachmentSelected = onAttachmentSelected,
        onTransactionSaved = onExpenseSaved,
        modifier = modifier
    )
}
