package com.zacle.spendtrack.feature.expense.add_edit_expense

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.common.util.ImageStorageManager
import com.zacle.spendtrack.core.domain.expense.AddExpenseUseCase
import com.zacle.spendtrack.core.domain.expense.GetExpenseUseCase
import com.zacle.spendtrack.core.domain.expense.UpdateExpenseUseCase
import com.zacle.spendtrack.core.domain.user.GetUserUseCase
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.model.util.period.toMonthlyPeriod
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.ext.isValidName
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
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val getExpenseUseCase: GetExpenseUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val imageStorageManager: ImageStorageManager,
    savedStateHandle: SavedStateHandle
): BaseViewModel<Unit, UiState<Unit>, TransactionUiAction, TransactionUiEvent>() {
    private val expenseId: String? = savedStateHandle[EXPENSE_ID_ARG]

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val expense = MutableStateFlow<Expense?>(null)

    init {
        viewModelScope.launch {
            getUserId()
            val userId = _uiState.value.userId
            if (expenseId != null) {
                getExpense(expenseId, userId)
            }
        }
    }

    private suspend fun getUserId() {
        getUserUseCase.execute(GetUserUseCase.Request).collect { result ->
            if (result is Result.Success) {
                val user = result.data.user
                if (user != null) {
                    _uiState.value = uiState.value.copy(userId = user.userId)
                } else {
                    submitSingleEvent(TransactionUiEvent.NavigateToLogin)
                }
            } else {
                submitSingleEvent(TransactionUiEvent.NavigateToLogin)
            }
        }
    }

    private suspend fun getExpense(expenseId: String, userId: String) {
        getExpenseUseCase.execute(GetExpenseUseCase.Request(userId, expenseId)).collectLatest { result ->
            if (result is Result.Success) {
                val expense = result.data.expense
                this.expense.value = expense
                if (expense != null) {
                    _uiState.value = uiState.value.copy(
                        name = expense.name,
                        description = expense.description,
                        amount = expense.amount.toInt(),
                        selectedCategory = expense.category,
                        transactionDate = expense.transactionDate,
                        receiptImage =
                            when {
                                expense.receiptUrl != null -> ImageData.UriImage(Uri.parse(expense.receiptUrl))
                                expense.localReceiptImagePath != null -> ImageData.LocalPathImage(
                                    expense.localReceiptImagePath!!
                                )
                                else -> null
                            }
                    )
                }
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
            if (expenseId != null) {
                updateExpense()
            } else {
                addExpense()
            }
        }
    }

    private suspend fun updateExpense() {
        formValidation(uiState.value.name, uiState.value.amount, uiState.value.selectedCategory)
        val errors = listOf(
            uiState.value.nameError,
            uiState.value.amountError,
            uiState.value.categoryError,
        )
        if (errors.any { it != null }) return

        val expense = expense.value
        var didImageChange = false
        if (expense?.receiptUrl != null) {
            val imageData = uiState.value.receiptImage
            didImageChange = if (imageData is ImageData.UriImage) {
                imageData.uri.toString() != expense.receiptUrl
            } else {
                true
            }
        }

        var localReceiptImagePath: String? = null
        if (didImageChange) {
            // Delete the old image
            expense?.localReceiptImagePath?.let {
                imageStorageManager.deleteImageLocally(it)
            }

            // Save the new image locally
            localReceiptImagePath = uiState.value.receiptImage?.let {
                imageStorageManager.saveImageLocally(it, "receipt_${System.currentTimeMillis()}")
            }
        }

        val updateExpense = expense?.copy(
            name = uiState.value.name,
            description = uiState.value.description,
            amount = uiState.value.amount.toDouble(),
            category = uiState.value.selectedCategory,
            transactionDate = uiState.value.transactionDate,
            receiptUrl = if (didImageChange) null else expense.receiptUrl,
            localReceiptImagePath = localReceiptImagePath
        )
        if (updateExpense != null) {
            updateExpenseUseCase.execute(
                UpdateExpenseUseCase.Request(
                    userId = uiState.value.userId,
                    expense = updateExpense,
                    period = uiState.value.transactionDate.toMonthlyPeriod()
                )
            )
            submitSingleEvent(TransactionUiEvent.NavigateToHome)
        }
    }

    private suspend fun addExpense() {
        formValidation(uiState.value.name, uiState.value.amount, uiState.value.selectedCategory)
        val errors = listOf(
            uiState.value.nameError,
            uiState.value.amountError,
            uiState.value.categoryError,
        )
        if (errors.any { it != null }) return

        val localReceiptImagePath = uiState.value.receiptImage?.let {
            imageStorageManager.saveImageLocally(it, "receipt_${System.currentTimeMillis()}")
        }

        val expense = Expense(
            userId = uiState.value.userId,
            name = uiState.value.name,
            description = uiState.value.description,
            amount = uiState.value.amount.toDouble(),
            category = uiState.value.selectedCategory,
            transactionDate = uiState.value.transactionDate,
            receiptUrl = null,
            localReceiptImagePath = localReceiptImagePath
        )
        addExpenseUseCase.execute(
            AddExpenseUseCase.Request(
                userId = uiState.value.userId,
                expense = expense,
                period = uiState.value.transactionDate.toMonthlyPeriod()
            )
        )
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

        if (!name.isValidName()) {
            _uiState.value = uiState.value.copy(nameError = NameError.INVALID)
            submitSingleEvent(TransactionUiEvent.InvalidNameError(NameError.INVALID.errorMessageResId))
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