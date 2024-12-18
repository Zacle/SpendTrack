package com.zacle.spendtrack.feature.home

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.HomeUseCase
import com.zacle.spendtrack.core.domain.user.GetUserUseCase
import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.model.util.period.toMonthlyPeriod
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiEvent
import com.zacle.spendtrack.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel
class  HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val converter: HomeConverter
): BaseViewModel<HomeModel, UiState<HomeModel>, HomeUiAction, UiEvent>() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserUseCase.execute(GetUserUseCase.Request).collect { result ->
                if (result is Result.Success) {
                    val user = result.data.user
                    if (user != null) {
                        _uiState.value = uiState.value.copy(
                            user = user,
                            profilePicture =
                                when {
                                    user.profilePictureUrl != null -> ImageData.UriImage(Uri.parse(user.profilePictureUrl))
                                    user.localProfilePictureUrl != null -> ImageData.LocalPathImage(
                                        user.localProfilePictureUrl!!
                                    )
                                    else -> null
                                }
                        )
                        submitAction(HomeUiAction.Load)
                    } else {
                        submitSingleEvent(HomeUiEvent.NavigateToLogin)
                    }
                } else {
                    submitSingleEvent(HomeUiEvent.NavigateToLogin)
                }
            }
        }
    }

    override fun initState(): UiState<HomeModel> = UiState.Loading

    override fun handleAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.Load -> load()
            is HomeUiAction.SetPeriod -> setPeriod(action.date)
            is HomeUiAction.SetDisplayTransactions -> setDisplayTransactions(action.shouldDisplayTransactions)
            is HomeUiAction.NavigateToProfile -> submitSingleEvent(HomeUiEvent.NavigateToProfile)
            is HomeUiAction.NavigateToExpense -> submitSingleEvent(HomeUiEvent.NavigateToExpense(action.expenseId))
            is HomeUiAction.NavigateToIncome -> submitSingleEvent(HomeUiEvent.NavigateToIncome(action.incomeId))
            is HomeUiAction.NavigateToBudgetDetails -> submitSingleEvent(HomeUiEvent.NavigateToBudgetDetails(action.budgetId))
            is HomeUiAction.NavigateToBudgets -> submitSingleEvent(HomeUiEvent.NavigateToBudgets)
            is HomeUiAction.NavigateToTransactions -> submitSingleEvent(HomeUiEvent.NavigateToTransactions)
        }
    }

    private fun load() {
        val userId = uiState.value.user?.userId ?: return
        viewModelScope.launch {
            homeUseCase.execute(
                HomeUseCase.Request(
                    userId = userId,
                    period = uiState.value.transactionPeriod,
                )
            ).collectLatest { result ->
                submitState(converter.convert(result))
            }
        }
    }

    private fun setPeriod(instant: Instant) {
        val period = instant.toMonthlyPeriod()
        _uiState.value = uiState.value.copy(selectedDate = instant, transactionPeriod = period)
        load()
    }

    private fun setDisplayTransactions(shouldDisplayTransactions: Boolean) {
        _uiState.value = uiState.value.copy(isTransactionViewActive = shouldDisplayTransactions)
    }
}