package com.zacle.spendtrack.feature.budget.add_edit_budget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.budget.AddBudgetUseCase
import com.zacle.spendtrack.core.domain.budget.GetBudgetUseCase
import com.zacle.spendtrack.core.domain.category.GetCategoriesUseCase
import com.zacle.spendtrack.core.domain.user.GetUserUseCase
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.model.util.period.toMonthlyPeriod
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.transaction.AmountError
import com.zacle.spendtrack.core.ui.transaction.CategoryError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditBudgetViewModel @Inject constructor(
    private val addBudgetUseCase: AddBudgetUseCase,
    private val getBudgetUseCase: GetBudgetUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    savedStateHandle: SavedStateHandle
): BaseViewModel<Unit, UiState<Unit>, AddEditBudgetUiAction, AddEditBudgetUiEvent>() {
    private val budgetId: String? = savedStateHandle[BUDGET_ID_ARG]

    private val _uiState = MutableStateFlow(AddEditBudgetUiState())
    val uiState: StateFlow<AddEditBudgetUiState> = _uiState.asStateFlow()

    private val budget = MutableStateFlow<Budget?>(null)

    init {
        viewModelScope.launch {
            getUserId()
            val userId = _uiState.value.userId
            if (budgetId != null) {
                getBudget(budgetId, userId)
            }
            getCategories()
        }
    }

    override fun initState(): UiState<Unit> = UiState.Loading

    override fun handleAction(action: AddEditBudgetUiAction) {
        when (action) {
            is AddEditBudgetUiAction.OnAmountChanged -> onAmountChanged(action.amount)
            is AddEditBudgetUiAction.OnCategorySelected -> onCategorySelected(action.category)
            is AddEditBudgetUiAction.OnBudgetAlertChanged -> onBudgetAlertChanged(action.budgetAlert)
            is AddEditBudgetUiAction.OnBudgetAlertPercentageChanged ->
                onBudgetAlertPercentageChanged(action.budgetAlertPercentage)
            is AddEditBudgetUiAction.OnRecurrentChanged -> onRecurrentChanged(action.recurrent)
            AddEditBudgetUiAction.OnSaveBudget -> onSaveBudget()
        }
    }

    private fun onAmountChanged(amount: Double) {
        _uiState.value = uiState.value.copy(amount = amount.toInt())
    }

    private fun onCategorySelected(category: Category) {
        _uiState.value = uiState.value.copy(selectedCategory = category)
    }

    private fun onBudgetAlertChanged(budgetAlert: Boolean) {
        _uiState.value = uiState.value.copy(budgetAlert = budgetAlert)
    }

    private fun onBudgetAlertPercentageChanged(budgetAlertPercentage: Int) {
        _uiState.value = uiState.value.copy(budgetAlertPercentage = budgetAlertPercentage)
    }

    private fun onRecurrentChanged(recurrent: Boolean) {
        _uiState.value = uiState.value.copy(recurrent = recurrent)
    }

    private fun onSaveBudget() {
        formValidation(uiState.value.amount, uiState.value.selectedCategory)
        val errors = listOf(
            uiState.value.amountError,
            uiState.value.categoryError,
        )
        if (errors.any { it != null }) return

        _uiState.value = uiState.value.copy(isUploading = true)

        val budget = budget.value
        val budgetToSave: Budget =
            budget?.copy(
                amount = uiState.value.amount.toDouble(),
                category = uiState.value.selectedCategory,
                recurrent = uiState.value.recurrent,
                budgetAlert = uiState.value.budgetAlert,
                budgetAlertPercentage = uiState.value.budgetAlertPercentage
            ) ?: Budget(
                    userId = uiState.value.userId,
                    amount = uiState.value.amount.toDouble(),
                    category = uiState.value.selectedCategory,
                    recurrent = uiState.value.recurrent,
                    budgetAlert = uiState.value.budgetAlert,
                    budgetAlertPercentage = uiState.value.budgetAlertPercentage
                )
        viewModelScope.launch {
            addBudgetUseCase.execute(
                AddBudgetUseCase.Request(
                    userId = uiState.value.userId,
                    budget = budgetToSave,
                    period = budgetToSave.budgetPeriod.toMonthlyPeriod()
                )
            )
        }
        _uiState.value = uiState.value.copy(isUploading = false)
        submitSingleEvent(AddEditBudgetUiEvent.NavigateBack)
    }

    private fun formValidation(amount: Int, category: Category) {
        verifyAmount(amount)
        if (uiState.value.amountError != null) return
        verifyCategory(category)
    }

    private fun verifyAmount(amount: Int) {
        if (amount <= 0) {
            _uiState.value = uiState.value.copy(amountError = AmountError.INVALID)
            submitSingleEvent(AddEditBudgetUiEvent.InvalidAmountError(AmountError.INVALID.errorMessageResId))
        } else {
            _uiState.value = uiState.value.copy(amountError = null)
        }
    }

    private fun verifyCategory(category: Category) {
        if (category.categoryId.isEmpty()) {
            _uiState.value = uiState.value.copy(categoryError = CategoryError.NOT_SELECTED)
            submitSingleEvent(AddEditBudgetUiEvent.CategoryNotSelectedError(CategoryError.NOT_SELECTED.errorMessageResId))
        } else {
            _uiState.value = uiState.value.copy(categoryError = null)
        }
    }

    private suspend fun getUserId() {
        val userIdResult = getUserUseCase.execute(GetUserUseCase.Request).first()
        if (userIdResult is Result.Success) {
            val userId = userIdResult.data.user?.userId
            if (userId != null) {
                _uiState.value = uiState.value.copy(userId = userId)
            } else {
                submitSingleEvent(AddEditBudgetUiEvent.NavigateToLogin)
            }
        } else {
            submitSingleEvent(AddEditBudgetUiEvent.NavigateToLogin)
        }
    }

    private suspend fun getCategories() {
        getCategoriesUseCase.execute(GetCategoriesUseCase.Request).collectLatest { result ->
            if (result is Result.Success) {
                val categories = result.data.categories
                _uiState.value = uiState.value.copy(categories = categories)
            }
        }
    }

    private suspend fun getBudget(budgetId: String, userId: String) {
        val budgetResult = getBudgetUseCase.execute(GetBudgetUseCase.Request(userId, budgetId)).first()
        if (budgetResult is Result.Success) {
            val budget = budgetResult.data.budget
            this.budget.value = budget
            if (budget != null) {
                _uiState.value = uiState.value.copy(
                    budgetId = budget.budgetId,
                    amount = budget.amount.toInt(),
                    selectedCategory = budget.category,
                    recurrent = budget.recurrent,
                    budgetAlert = budget.budgetAlert,
                    budgetAlertPercentage = budget.budgetAlertPercentage
                )
            }
        }
    }
}