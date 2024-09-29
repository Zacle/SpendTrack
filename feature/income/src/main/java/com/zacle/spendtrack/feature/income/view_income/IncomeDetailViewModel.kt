package com.zacle.spendtrack.feature.income.view_income

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.income.DeleteIncomeUseCase
import com.zacle.spendtrack.core.domain.income.GetIncomeUseCase
import com.zacle.spendtrack.core.domain.user.GetUserUseCase
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.Transaction
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.model.util.period.toMonthlyPeriod
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.transaction.TransactionDetailUiAction
import com.zacle.spendtrack.core.ui.transaction.TransactionDetailUiEvent
import com.zacle.spendtrack.core.ui.transaction.TransactionDetailUiState
import com.zacle.spendtrack.core.ui.transaction.TransactionModel
import com.zacle.spendtrack.feature.income.add_edit_income.INCOME_ID_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomeDetailViewModel @Inject constructor(
    private val getIncomeUseCase: GetIncomeUseCase,
    private val deleteIncomeUseCase: DeleteIncomeUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val converter: IncomeDetailConverter,
    savedStateHandle: SavedStateHandle
): BaseViewModel<TransactionModel, UiState<TransactionModel>, TransactionDetailUiAction, TransactionDetailUiEvent>() {
    // Retrieve the Income id, it should not be null
    private val incomeId: String = requireNotNull(savedStateHandle[INCOME_ID_ARG])

    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState: StateFlow<TransactionDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserId()
            val userId = _uiState.value.userId
            load(userId)
        }
    }

    override fun initState(): UiState<TransactionModel> = UiState.Loading

    override fun handleAction(action: TransactionDetailUiAction) {
        when (action) {
            TransactionDetailUiAction.OnEditPressed ->
                submitSingleEvent(TransactionDetailUiEvent.NavigateToEditTransaction(incomeId))
            TransactionDetailUiAction.OnDeletePressed ->
                _uiState.value = uiState.value.copy(shouldDisplayRemoveTransactionDialog = true)
            TransactionDetailUiAction.OnDeleteDismissed ->
                _uiState.value = uiState.value.copy(shouldDisplayRemoveTransactionDialog = false)
            is TransactionDetailUiAction.OnDeleteConfirmed ->
                deleteIncome(action.transaction)
            TransactionDetailUiAction.OnDismissTransactionDeletedDialog ->
                _uiState.value = uiState.value.copy(isTransactionDeletedDialogDisplaying = false)
        }
    }

    private suspend fun getUserId() {
        val userIdResult = getUserUseCase.execute(GetUserUseCase.Request).first()
        if (userIdResult is Result.Success) {
            val userId = userIdResult.data.user?.userId
            if (userId != null) {
                _uiState.value = uiState.value.copy(userId = userId)
            } else {
                submitSingleEvent(TransactionDetailUiEvent.NavigateToLogin)
            }
        } else {
            submitSingleEvent(TransactionDetailUiEvent.NavigateToLogin)
        }
    }

    private suspend fun load(userId: String) {
        val incomeResult = getIncomeUseCase.execute(GetIncomeUseCase.Request(userId, incomeId)).first()
        submitState(converter.convert(incomeResult))
    }

    private fun deleteIncome(transaction: Transaction) {
        viewModelScope.launch {
            val userId = _uiState.value.userId
            val period = transaction.transactionDate.toMonthlyPeriod()
            deleteIncomeUseCase.execute(DeleteIncomeUseCase.Request(userId, transaction as Income, period))
            _uiState.value = uiState.value.copy(
                shouldDisplayRemoveTransactionDialog = false,
                isTransactionDeleted = true,
                isTransactionDeletedDialogDisplaying = true
            )
        }
    }
}