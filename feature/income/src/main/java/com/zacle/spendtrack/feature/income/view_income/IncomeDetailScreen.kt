package com.zacle.spendtrack.feature.income.view_income

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zacle.spendtrack.core.designsystem.component.EmptyScreen
import com.zacle.spendtrack.core.designsystem.component.RemoveTransactionModalSheet
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.TransactionDeletedDialog
import com.zacle.spendtrack.core.designsystem.component.TransactionDetailScreen
import com.zacle.spendtrack.core.designsystem.component.noRippleEffect
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.model.Transaction
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.CommonScreen
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.composition_local.LocalCurrency
import com.zacle.spendtrack.core.ui.transaction.TransactionDetailUiAction
import com.zacle.spendtrack.core.ui.transaction.TransactionDetailUiEvent
import com.zacle.spendtrack.core.ui.transaction.TransactionDetailUiState
import com.zacle.spendtrack.core.ui.transaction.TransactionModel

@Composable
fun IncomeDetailRoute(
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToEditIncome: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IncomeDetailViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val stateHolder by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect { event ->
            when (event) {
                is TransactionDetailUiEvent.NavigateToLogin -> navigateToLogin()
                is TransactionDetailUiEvent.NavigateToEditTransaction -> navigateToEditIncome(event.transactionId)
            }
        }
    }

    IncomeDetailScreen(
        uiState = uiState,
        stateHolder = stateHolder,
        onNavigateBack = navigateBack,
        onEditPressed = { viewModel.submitAction(TransactionDetailUiAction.OnEditPressed) },
        onDeletePressed = { viewModel.submitAction(TransactionDetailUiAction.OnDeletePressed) },
        onDeleteDismissed = { viewModel.submitAction(TransactionDetailUiAction.OnDeleteDismissed) },
        onDeleteConfirmed = { viewModel.submitAction(TransactionDetailUiAction.OnDeleteConfirmed(it)) },
        onDismissTransactionDeletedDialog = { viewModel.submitAction(TransactionDetailUiAction.OnDismissTransactionDeletedDialog) },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun IncomeDetailScreen(
    uiState: UiState<TransactionModel>,
    stateHolder: TransactionDetailUiState,
    onNavigateBack: () -> Unit,
    onEditPressed: () -> Unit,
    onDeletePressed: () -> Unit,
    onDeleteDismissed: () -> Unit,
    onDeleteConfirmed: (Transaction) -> Unit,
    onDismissTransactionDeletedDialog: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier
) {
    Scaffold(
        topBar = {
            STTopAppBar(
                title = { Text(text = stringResource(R.string.detail_transaction)) },
                navigationIcon = {
                    Icon(
                        imageVector = SpendTrackIcons.arrowBack,
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier.noRippleEffect { onNavigateBack() }
                    )
                },
                actionIcon = {
                    Icon(
                        painter = painterResource(R.drawable.trash),
                        contentDescription = stringResource(R.string.remove_transaction_title),
                        modifier = Modifier
                            .size(24.dp)
                            .noRippleEffect { onDeletePressed() },
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                },
                containerColor = Color(0xFFF3CC42),
                titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onTertiary
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
        containerColor = Color.Transparent
    ) {
        CommonScreen(uiState) { transactionModel ->
            if (transactionModel.transaction == null) {
                EmptyScreen(
                    message = stringResource(R.string.income_not_found),
                    description = stringResource(R.string.income_not_found_description)
                )
            } else {
                IncomeDetailContent(
                    transaction = transactionModel.transaction!!,
                    stateHolder = stateHolder,
                    onEditPressed = onEditPressed,
                    onDeleteDismissed = onDeleteDismissed,
                    onDeleteConfirmed = onDeleteConfirmed,
                    onDismissTransactionDeletedDialog = onDismissTransactionDeletedDialog
                )
            }
        }
    }
}

@Composable
fun IncomeDetailContent(
    transaction: Transaction,
    stateHolder: TransactionDetailUiState,
    onEditPressed: () -> Unit,
    onDeleteDismissed: () -> Unit,
    onDeleteConfirmed: (Transaction) -> Unit,
    onDismissTransactionDeletedDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val receiptUriImage =
        when {
            transaction.receiptUrl != null -> ImageData.UriImage(Uri.parse(transaction.receiptUrl))
            transaction.localReceiptImagePath != null -> ImageData.LocalPathImage(
                transaction.localReceiptImagePath!!
            )
            else -> null
        }
    TransactionDetailScreen(
        amount = transaction.amount.toInt(),
        name = transaction.name,
        date = transaction.transactionDate,
        type = stringResource(R.string.income),
        key = transaction.category.key,
        description = transaction.description,
        receiptUriImage = receiptUriImage,
        onEdit = onEditPressed,
        isTransactionDeleted = stateHolder.isTransactionDeleted,
        color = Color(0xFFF3CC42),
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        currency = LocalCurrency.current,
        modifier = modifier
    )
    if (stateHolder.shouldDisplayRemoveTransactionDialog) {
        RemoveTransactionModalSheet(
            onDismiss = onDeleteDismissed,
            onConfirm = { onDeleteConfirmed(transaction) }
        )
    }
    if (stateHolder.isTransactionDeletedDialogDisplaying) {
        TransactionDeletedDialog(
            onDismiss = onDismissTransactionDeletedDialog
        )
    }
}
