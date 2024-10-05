package com.zacle.spendtrack.feature.budget.view_budget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.budget.DeleteBudgetUseCase
import com.zacle.spendtrack.core.domain.budget.GetBudgetDetailsUseCase
import com.zacle.spendtrack.core.domain.budget.GetBudgetUseCase
import com.zacle.spendtrack.core.domain.user.GetUserUseCase
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.model.util.period.toMonthlyPeriod
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.feature.budget.add_edit_budget.BUDGET_ID_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetDetailViewModel @Inject constructor(
    private val getBudgetUseCase: GetBudgetUseCase,
    private val getBudgetDetailsUseCase: GetBudgetDetailsUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val converter: BudgetDetailConverter,
    savedStateHandle: SavedStateHandle
): BaseViewModel<BudgetDetailModel, UiState<BudgetDetailModel>, BudgetDetailUiAction, BudgetDetailUiEvent>() {
    // Retrieve the Budget id, it should not be null
    private val budgetId: String = requireNotNull(savedStateHandle[BUDGET_ID_ARG])

    private val _uiState = MutableStateFlow(BudgetDetailUiState())
    val uiState: StateFlow<BudgetDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserId()
            val userId = _uiState.value.userId
            loadBudget(userId, budgetId)
        }
    }

    override fun initState(): UiState<BudgetDetailModel> = UiState.Loading

    override fun handleAction(action: BudgetDetailUiAction) {
        when (action) {
            is BudgetDetailUiAction.OnEditPressed ->
                submitSingleEvent(BudgetDetailUiEvent.NavigateToEditBudget(budgetId))
            BudgetDetailUiAction.OnDeletePressed ->
                _uiState.value = uiState.value.copy(shouldDisplayRemoveBudgetDialog = true)
            BudgetDetailUiAction.OnDeleteDismissed ->
                _uiState.value = uiState.value.copy(shouldDisplayRemoveBudgetDialog = false)
            is BudgetDetailUiAction.OnDeleteConfirmed -> deleteBudget(action.budget)
            is BudgetDetailUiAction.OnExpenseClicked ->
                submitSingleEvent(BudgetDetailUiEvent.NavigateToExpenseDetail(action.expenseId))
            is BudgetDetailUiAction.OnIncomeClicked ->
                submitSingleEvent(BudgetDetailUiEvent.NavigateToIncomeDetail(action.incomeId))
        }
    }

    private suspend fun getUserId() {
        val userIdResult = getUserUseCase.execute(GetUserUseCase.Request).first()
        if (userIdResult is Result.Success) {
            val userId = userIdResult.data.user?.userId
            if (userId != null) {
                _uiState.value = uiState.value.copy(userId = userId)
            } else {
                submitSingleEvent(BudgetDetailUiEvent.NavigateToLogin)
            }
        } else {
            submitSingleEvent(BudgetDetailUiEvent.NavigateToLogin)
        }
    }

    private fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            val userId = uiState.value.userId
            deleteBudgetUseCase.execute(DeleteBudgetUseCase.Request(userId, budget))
            _uiState.value = uiState.value.copy(shouldDisplayRemoveBudgetDialog = false)
            submitSingleEvent(BudgetDetailUiEvent.NavigateBack)
        }
    }

    private suspend fun loadBudget(userId: String, budgetId: String) {
        val budgetResult = getBudgetUseCase.execute(GetBudgetUseCase.Request(userId, budgetId)).first()
        if (budgetResult is Result.Success) {
            val budget = budgetResult.data.budget
            if (budget != null) {
                getBudgetDetailsUseCase.execute(
                    GetBudgetDetailsUseCase.Request(
                        userId = userId,
                        budgetId = budgetId,
                        categoryId = budget.category.categoryId,
                        budgetPeriod = budget.budgetPeriod.toMonthlyPeriod()
                    )
                ).collectLatest {
                    submitState(converter.convert(it))
                }
            }
        }
    }
}