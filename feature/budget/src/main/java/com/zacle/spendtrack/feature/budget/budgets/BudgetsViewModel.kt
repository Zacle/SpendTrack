package com.zacle.spendtrack.feature.budget.budgets

import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.budget.GetBudgetsUseCase
import com.zacle.spendtrack.core.domain.datastore.GetUserDataUseCase
import com.zacle.spendtrack.core.domain.user.GetUserUseCase
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.model.util.period.toMonthlyPeriod
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import javax.inject.Inject

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val converter: BudgetsConverter
): BaseViewModel<BudgetsModel, UiState<BudgetsModel>, BudgetsUiAction, BudgetsUiEvent>() {

    private val _uiState = MutableStateFlow(BudgetsUiState())
    val uiState: StateFlow<BudgetsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserId()
            submitAction(BudgetsUiAction.Load)
        }
    }

    override fun initState(): UiState<BudgetsModel> = UiState.Loading

    override fun handleAction(action: BudgetsUiAction) {
        when (action) {
            BudgetsUiAction.Load -> {
                loadBudgets()
            }
            BudgetsUiAction.OnNextMonthPressed -> onNextMonthPressed()
            BudgetsUiAction.OnPreviousMonthPressed -> onPreviousMonthPressed()
        }
    }

    private suspend fun getUserId() {
        val userIdResult = getUserUseCase.execute(GetUserUseCase.Request).first()
        if (userIdResult is Result.Success) {
            val userId = userIdResult.data.user?.userId
            if (userId != null) {
                _uiState.value = uiState.value.copy(userId = userId)
            } else {
                submitSingleEvent(BudgetsUiEvent.NavigateToLogin)
            }
        } else {
            submitSingleEvent(BudgetsUiEvent.NavigateToLogin)
        }
    }

    private fun loadBudgets() {
        val userId = uiState.value.userId
        val period = uiState.value.selectedMonth.toInstant(TimeZone.currentSystemDefault()).toMonthlyPeriod()
        viewModelScope.launch {
            combine(
                getBudgetsUseCase.execute(
                    GetBudgetsUseCase.Request(
                        userId = userId,
                        budgetPeriod = period
                    )
                ),
                getUserDataUseCase.execute(GetUserDataUseCase.Request)
            ) { budgetsResult, userDataResult ->
                if (userDataResult is Result.Success) {
                    _uiState.value = uiState.value.copy(
                        currencyCode = userDataResult.data.userData.currencyCode
                    )
                }
                budgetsResult
            }.collectLatest { result ->
                submitState(converter.convert(result))
            }
        }
    }

    private fun onNextMonthPressed() {
        val selectedMonth = uiState.value.selectedMonth
        val nextMonth = selectedMonth.toJavaLocalDateTime().plusMonths(1).toKotlinLocalDateTime()
        _uiState.value = uiState.value.copy(selectedMonth = nextMonth)
        loadBudgets()
    }

    private fun onPreviousMonthPressed() {
        val selectedMonth = uiState.value.selectedMonth
        val previousMonth = selectedMonth.toJavaLocalDateTime().minusMonths(1).toKotlinLocalDateTime()
        _uiState.value = uiState.value.copy(selectedMonth = previousMonth)
        loadBudgets()
    }
}