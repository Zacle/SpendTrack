package com.zacle.spendtrack.feature.transaction.financial_report

import android.os.CountDownTimer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.user.GetUserUseCase
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.model.util.FilterState
import com.zacle.spendtrack.core.model.util.SortOrder
import com.zacle.spendtrack.core.model.util.period.toMonthlyPeriod
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime
import javax.inject.Inject
import com.zacle.spendtrack.core.domain.HomeUseCase as FinancialReportUseCase

@HiltViewModel
class FinancialReportViewModel @Inject constructor(
    private val financialReportUseCase: FinancialReportUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val financialReportConverter: FinancialReportConverter,
    savedStateHandle: SavedStateHandle
): BaseViewModel<FinancialReportModel, UiState<FinancialReportModel>, FinancialReportUiAction, FinancialReportUiEvent>() {
    private val month: Int = requireNotNull(savedStateHandle[MONTH_ARG_KEY])
    private val year: Int = requireNotNull(savedStateHandle[YEAR_ARG_KEY])

    private val loggedInUserId = MutableStateFlow("")

    // Holds the progress for the current page (0 to 1.0 for percentage)
    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    // Holds the current page index
    private val _currentPage = MutableStateFlow(0)
    val currentPage = _currentPage.asStateFlow()

    private var timer: CountDownTimer? = null

    // Duration per page (5 seconds)
    private val durationPerPage = 5000L
    private val interval = 50L // Update progress every 50 milliseconds

    init {
        viewModelScope.launch {
            getUserId()
            submitAction(FinancialReportUiAction.Load)
            startTimer()
        }
    }

    override fun initState(): UiState<FinancialReportModel> = UiState.Loading

    override fun handleAction(action: FinancialReportUiAction) {
        when (action) {
            is FinancialReportUiAction.Load -> loadFinancialReport()
        }
    }

    private suspend fun getUserId() {
        val userIdResult = getUserUseCase.execute(GetUserUseCase.Request).first()
        if (userIdResult is Result.Success) {
            val userId = userIdResult.data.user?.userId
            if (userId != null) {
                loggedInUserId.value = userId
            } else {
                submitSingleEvent(FinancialReportUiEvent.NavigateToLogin)
            }
        } else {
            submitSingleEvent(FinancialReportUiEvent.NavigateToLogin)
        }
    }

    private fun loadFinancialReport() {
        val financialReportPeriod = getFinancialReportDate()
            .toInstant(TimeZone.currentSystemDefault())
            .toMonthlyPeriod()

        viewModelScope.launch {
            financialReportUseCase.execute(
                FinancialReportUseCase.Request(
                    userId = loggedInUserId.value,
                    period = financialReportPeriod,
                    filterState = FilterState(),
                    sortOrder = SortOrder.NEWEST
                )
            ).collectLatest {
                submitState(financialReportConverter.convert(it))
            }
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(durationPerPage, interval) {
            override fun onTick(millisUntilFinished: Long) {
                // Update progress based on time remaining
                val progressValue = 1f - (millisUntilFinished / durationPerPage.toFloat())
                _progress.value = progressValue
            }

            override fun onFinish() {
                // Move to the next page and reset progress
                _currentPage.value = (_currentPage.value + 1) % 3
                _progress.value = 0f
                startTimer() // Restart the timer for the next page
            }
        }.start()
    }

    fun getFinancialReportDate() =
        LocalDateTime
            .of(year, month, 1, 0, 0)
            .toKotlinLocalDateTime()
}