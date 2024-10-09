package com.zacle.spendtrack.feature.transaction

import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.TransactionsUseCase
import com.zacle.spendtrack.core.domain.category.GetCategoriesUseCase
import com.zacle.spendtrack.core.domain.user.GetUserUseCase
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.model.util.SortOrder
import com.zacle.spendtrack.core.model.util.period.toMonthlyPeriod
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionsUseCase: TransactionsUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val transactionConverter: TransactionConverter
): BaseViewModel<TransactionModel, UiState<TransactionModel>, TransactionUiAction, TransactionUiEvent>() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserId()
            loadCategories()
            loadTransactions()
        }
    }

    override fun initState(): UiState<TransactionModel> = UiState.Loading

    override fun handleAction(action: TransactionUiAction) {
        when (action) {
            is TransactionUiAction.OnFilterTransactionPressed ->
                _uiState.value = uiState.value.copy(shouldDisplayFilterTransaction = true)
            is TransactionUiAction.OnFilterTransactionDismissed ->
                _uiState.value = uiState.value.copy(shouldDisplayFilterTransaction = false)
            is TransactionUiAction.OnFilterTransactionApplied ->
                onTransactionFilterApplied(action.transactionUiState)
            is TransactionUiAction.OnMonthPeriodPressed ->
                _uiState.value = uiState.value.copy(shouldDisplayMonthPeriod = true)
            is TransactionUiAction.OnMonthPeriodDismissed ->
                _uiState.value = uiState.value.copy(shouldDisplayMonthPeriod = false)
            is TransactionUiAction.OnMonthPeriodApplied ->
                onMonthPeriodApplied(action.selectedMonth)
            is TransactionUiAction.OnNavigateToExpense ->
                submitSingleEvent(TransactionUiEvent.NavigateToExpense(action.expenseId))
            is TransactionUiAction.OnNavigateToIncome ->
                submitSingleEvent(TransactionUiEvent.NavigateToIncome(action.incomeId))
            is TransactionUiAction.OnNavigateToFinancialReport ->
                submitSingleEvent(TransactionUiEvent.NavigateToFinancialReport(action.month, action.year))
        }
    }

    private suspend fun getUserId() {
        val userIdResult = getUserUseCase.execute(GetUserUseCase.Request).first()
        if (userIdResult is Result.Success) {
            val userId = userIdResult.data.user?.userId
            if (userId != null) {
                _uiState.value = uiState.value.copy(userId = userId)
            } else {
                submitSingleEvent(TransactionUiEvent.NavigateToLogin)
            }
        } else {
            submitSingleEvent(TransactionUiEvent.NavigateToLogin)
        }
    }

    private suspend fun loadCategories() {
        val categoriesResult = getCategoriesUseCase.execute(GetCategoriesUseCase.Request).first()
        if (categoriesResult is Result.Success) {
            _uiState.value = uiState.value.copy(categories = categoriesResult.data.categories)
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            val userId = uiState.value.userId
            val currentState = uiState.value
            transactionsUseCase.execute(
                TransactionsUseCase.Request(
                    userId = userId,
                    period = currentState.selectedMonth.toMonthlyPeriod(),
                    filterState = currentState.filterState,
                    sortOrder = currentState.sortOrder
                )
            ).collectLatest { response ->
                submitState(transactionConverter.convert(response))
            }
        }
    }

    private fun onTransactionFilterApplied(transactionUiState: TransactionUiState) {
        var countFilterApplied = 0
        if (transactionUiState.filterState.includeIncomes && !transactionUiState.filterState.includeExpenses) countFilterApplied++
        if (transactionUiState.filterState.includeExpenses && !transactionUiState.filterState.includeIncomes) countFilterApplied++
        if (transactionUiState.sortOrder != SortOrder.NEWEST) countFilterApplied++
        if (transactionUiState.filterState.categoryIds.isNotEmpty()) countFilterApplied++

        _uiState.value = uiState.value.copy(
            filterState = transactionUiState.filterState,
            sortOrder = transactionUiState.sortOrder,
            numberOfTransactionsSelected = countFilterApplied
        )
        loadTransactions()
        _uiState.value = uiState.value.copy(shouldDisplayFilterTransaction = false)
    }

    private fun onMonthPeriodApplied(selectedMonth: Instant) {
        _uiState.value = uiState.value.copy(selectedMonth = selectedMonth)
        loadTransactions()
        _uiState.value = uiState.value.copy(shouldDisplayMonthPeriod = false)
    }
}