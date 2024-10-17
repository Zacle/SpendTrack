package com.zacle.spendtrack.feature.report

import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.GetReportUseCase
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getReportUseCase: GetReportUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val reportConverter: ReportConverter
): BaseViewModel<ReportModel, UiState<ReportModel>, ReportUiAction, ReportUiEvent>() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserId()
            loadReport()
        }
    }

    override fun initState(): UiState<ReportModel> = UiState.Loading

    override fun handleAction(action: ReportUiAction) {
        when (action) {
            is ReportUiAction.OnReportPeriodPressed ->
                _uiState.value = uiState.value.copy(shouldShowSelectReportPeriodDialog = true)
            is ReportUiAction.OnReportPeriodDismissed ->
                _uiState.value = uiState.value.copy(shouldShowSelectReportPeriodDialog = false)
            is ReportUiAction.OnReportPeriodConfirmed ->
                onReportPeriodConfirmed(action.date)
            is ReportUiAction.OnRecordTransactionTypeChanged ->
                _uiState.value = uiState.value.copy(recordTransactionType = action.type)
            is ReportUiAction.OnChartTypeChanged ->
                _uiState.value = uiState.value.copy(chartType = action.type)
            is ReportUiAction.OnShowTransactions ->
                _uiState.value = uiState.value.copy(shouldShowTransactions = action.showTransactions)
            is ReportUiAction.OnNavigateToExpense ->
                submitSingleEvent(ReportUiEvent.NavigateToExpense(action.expenseId))
            is ReportUiAction.OnNavigateToIncome ->
                submitSingleEvent(ReportUiEvent.NavigateToIncome(action.incomeId))
        }
    }

    private suspend fun getUserId() {
        val userIdResult = getUserUseCase.execute(GetUserUseCase.Request).first()
        if (userIdResult is Result.Success) {
            val userId = userIdResult.data.user?.userId
            if (userId != null) {
                _uiState.value = uiState.value.copy(userId = userId)
            } else {
                submitSingleEvent(ReportUiEvent.NavigateToLogin)
            }
        } else {
            submitSingleEvent(ReportUiEvent.NavigateToLogin)
        }
    }

    private fun loadReport() {
        viewModelScope.launch {
            val userId = uiState.value.userId
            val period = uiState.value.selectedPeriod.toMonthlyPeriod()
            getReportUseCase.execute(
                GetReportUseCase.Request(userId, period)
            ).collectLatest { response ->
                submitState(reportConverter.convert(response))
            }
        }
    }

    private fun onReportPeriodConfirmed(date: Instant) {
        _uiState.value = uiState.value.copy(selectedPeriod = date)
        loadReport()
        _uiState.value = uiState.value.copy(shouldShowSelectReportPeriodDialog = false)
    }
}