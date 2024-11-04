package com.zacle.spendtrack.feature.income.add_edit_income

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.common.util.ImageStorageManager
import com.zacle.spendtrack.core.domain.category.GetCategoriesUseCase
import com.zacle.spendtrack.core.domain.income.AddIncomeUseCase
import com.zacle.spendtrack.core.domain.income.GetIncomeUseCase
import com.zacle.spendtrack.core.domain.income.UpdateIncomeUseCase
import com.zacle.spendtrack.core.domain.user.GetUserUseCase
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.model.util.period.toMonthlyPeriod
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.transaction.AmountError
import com.zacle.spendtrack.core.ui.transaction.CategoryError
import com.zacle.spendtrack.core.ui.transaction.NameError
import com.zacle.spendtrack.core.ui.transaction.TransactionUiAction
import com.zacle.spendtrack.core.ui.transaction.TransactionUiEvent
import com.zacle.spendtrack.core.ui.transaction.TransactionUiState
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
class AddEditIncomeViewModel @Inject constructor(
    private val addIncomeUseCase: AddIncomeUseCase,
    private val updateIncomeUseCase: UpdateIncomeUseCase,
    private val getIncomeUseCase: GetIncomeUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val imageStorageManager: ImageStorageManager,
    savedStateHandle: SavedStateHandle
): BaseViewModel<Unit, UiState<Unit>, TransactionUiAction, TransactionUiEvent>() {
    private val incomeId: String? = savedStateHandle[INCOME_ID_ARG]

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val income = MutableStateFlow<Income?>(null)

    init {
        viewModelScope.launch {
            getUserId()
            val userId = _uiState.value.userId
            if (incomeId != null) {
                getIncome(incomeId, userId)
            }
            getCategories()
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

    private suspend fun getIncome(incomeId: String, userId: String) {
        val incomeResult = getIncomeUseCase.execute(GetIncomeUseCase.Request(userId, incomeId)).first()
        if (incomeResult is Result.Success) {
            val income = incomeResult.data.income
            this.income.value = income
            if (income != null) {
                _uiState.value = uiState.value.copy(
                    name = income.name,
                    description = income.description,
                    amount = income.amount.toInt(),
                    selectedCategory = income.category,
                    transactionDate = income.transactionDate,
                    receiptImage =
                        when {
                            income.receiptUrl != null -> ImageData.UriImage(Uri.parse(income.receiptUrl))
                            income.localReceiptImagePath != null -> ImageData.LocalPathImage(
                                income.localReceiptImagePath!!
                            )
                            else -> null
                        }
                )
            }
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

    override fun initState(): UiState<Unit> = UiState.Loading

    override fun handleAction(action: TransactionUiAction) {
        when (action) {
            is TransactionUiAction.OnAmountChanged -> onAmountChanged(action.amount)
            is TransactionUiAction.OnCategorySelected -> onCategorySelected(action.category)
            is TransactionUiAction.OnDateSelected -> onDateSelected(action.date)
            is TransactionUiAction.OnDescriptionChanged -> onDescriptionChanged(action.description)
            is TransactionUiAction.OnNameChanged -> onNameChanged(action.name)
            is TransactionUiAction.OnAttachmentSelected -> onAttachmentSelected(action.attachment)
            TransactionUiAction.OnSaveTransaction -> onSaveTransaction()
        }
    }

    private fun onAmountChanged(amount: Double) {
        _uiState.value = uiState.value.copy(amount = amount.toInt())
    }

    private fun onCategorySelected(category: Category) {
        _uiState.value = uiState.value.copy(selectedCategory = category)
    }

    private fun onDateSelected(date: Instant) {
        _uiState.value = uiState.value.copy(transactionDate = date)
    }

    private fun onDescriptionChanged(description: String) {
        _uiState.value = uiState.value.copy(description = description)
    }

    private fun onNameChanged(name: String) {
        _uiState.value = uiState.value.copy(name = name)
    }

    private fun onAttachmentSelected(attachment: ImageData?) {
        _uiState.value = uiState.value.copy(receiptImage = attachment)
    }

    private fun onSaveTransaction() {
        viewModelScope.launch {
            if (incomeId != null) {
                updateIncome()
            } else {
                addIncome()
            }
        }
    }

    private suspend fun addIncome() {
        formValidation(uiState.value.name, uiState.value.amount, uiState.value.selectedCategory)
        val errors = listOf(
            uiState.value.nameError,
            uiState.value.amountError,
            uiState.value.categoryError,
        )
        if (errors.any { it != null }) return

        _uiState.value = uiState.value.copy(isUploading = true)

        val localReceiptImagePath = uiState.value.receiptImage?.let {
            imageStorageManager.saveImageLocally(it, "receipt_${System.currentTimeMillis()}")
        }

        val income = Income(
            userId = uiState.value.userId,
            name = uiState.value.name,
            description = uiState.value.description,
            amount = uiState.value.amount.toDouble(),
            category = uiState.value.selectedCategory,
            transactionDate = uiState.value.transactionDate,
            receiptUrl = null,
            localReceiptImagePath = localReceiptImagePath
        )
        addIncomeUseCase.execute(
            AddIncomeUseCase.Request(
                userId = uiState.value.userId,
                income = income,
                period = uiState.value.transactionDate.toMonthlyPeriod()
            )
        )
        _uiState.value = uiState.value.copy(isUploading = false)
        submitSingleEvent(TransactionUiEvent.NavigateToTransactionDetail(income.id))
    }

    private suspend fun updateIncome() {
        formValidation(uiState.value.name, uiState.value.amount, uiState.value.selectedCategory)
        val errors = listOf(
            uiState.value.nameError,
            uiState.value.amountError,
            uiState.value.categoryError,
        )
        if (errors.any { it != null }) return

        _uiState.value = uiState.value.copy(isUploading = true)

        val income = income.value
        val didImageChange: Boolean
        if (uiState.value.receiptImage != null) {
            val imageData = uiState.value.receiptImage
            didImageChange = when (imageData) {
                is ImageData.UriImage -> imageData.uri.toString() != income?.receiptUrl
                is ImageData.LocalPathImage -> imageData.path != income?.localReceiptImagePath
                else -> true
            }
        } else {
            didImageChange = true
        }

        var localReceiptImagePath: String? = income?.localReceiptImagePath
        if (didImageChange) {
            // Delete the old image
            income?.localReceiptImagePath?.let {
                imageStorageManager.deleteImageLocally(it)
            }

            // Save the new image locally
            localReceiptImagePath = uiState.value.receiptImage?.let {
                imageStorageManager.saveImageLocally(it, "receipt_${System.currentTimeMillis()}")
            }
        }

        val updateIncome = income?.copy(
            name = uiState.value.name,
            description = uiState.value.description,
            amount = uiState.value.amount.toDouble(),
            category = uiState.value.selectedCategory,
            transactionDate = uiState.value.transactionDate,
            receiptUrl = if (didImageChange) null else income.receiptUrl,
            localReceiptImagePath = localReceiptImagePath
        )
        if (updateIncome != null) {
            updateIncomeUseCase.execute(
                UpdateIncomeUseCase.Request(
                    userId = uiState.value.userId,
                    income = updateIncome,
                    period = uiState.value.transactionDate.toMonthlyPeriod()
                )
            )
            _uiState.value = uiState.value.copy(isUploading = false)
            submitSingleEvent(TransactionUiEvent.NavigateBack)
        }
        _uiState.value = uiState.value.copy(isUploading = false)
    }

    private fun formValidation(name: String, amount: Int, category: Category) {
        verifyName(name)
        if (uiState.value.nameError != null) return
        verifyAmount(amount)
        if (uiState.value.amountError != null) return
        verifyCategory(category)
    }

    private fun verifyName(name: String) {
        if (name.isBlank()) {
            _uiState.value = uiState.value.copy(nameError = NameError.BLANK)
            submitSingleEvent(TransactionUiEvent.BlankNameError(NameError.BLANK.errorMessageResId))
        } else {
            _uiState.value = uiState.value.copy(nameError = null)
        }

        if (name.length < 3) {
            _uiState.value = uiState.value.copy(nameError = NameError.SHORT)
            submitSingleEvent(TransactionUiEvent.ShortNameError(NameError.SHORT.errorMessageResId))
        } else {
            _uiState.value = uiState.value.copy(nameError = null)
        }
    }

    private fun verifyAmount(amount: Int) {
        if (amount <= 0) {
            _uiState.value = uiState.value.copy(amountError = AmountError.INVALID)
            submitSingleEvent(TransactionUiEvent.InvalidAmountError(AmountError.INVALID.errorMessageResId))
        } else {
            _uiState.value = uiState.value.copy(amountError = null)
        }
    }

    private fun verifyCategory(category: Category) {
        if (category.categoryId.isEmpty()) {
            _uiState.value = uiState.value.copy(categoryError = CategoryError.NOT_SELECTED)
            submitSingleEvent(TransactionUiEvent.CategoryNotSelectedError(CategoryError.NOT_SELECTED.errorMessageResId))
        } else {
            _uiState.value = uiState.value.copy(categoryError = null)
        }
    }
}