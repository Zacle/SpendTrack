package com.zacle.spendtrack

import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.GetUserDataAndAuthStateUseCase
import com.zacle.spendtrack.core.domain.category.GetCategoriesUseCase
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiEvent
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.data.UserStateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getUserDataAndAuthStateUseCase: GetUserDataAndAuthStateUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val converter: MainActivityConverter
): BaseViewModel<UserStateModel, UiState<UserStateModel>, MainActivityUiAction, UiEvent>() {

    override fun initState(): UiState<UserStateModel>  = UiState.Loading

    init {
        submitAction(MainActivityUiAction.Load)
    }

    override fun handleAction(action: MainActivityUiAction) {
        when (action) {
            is MainActivityUiAction.Load -> {
                loadUserData()
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            combine(
                getUserDataAndAuthStateUseCase.execute(GetUserDataAndAuthStateUseCase.Request),
                getCategoriesUseCase.execute(GetCategoriesUseCase.Request)
            ) { userDataResponse, _ ->
                converter.convert(userDataResponse)
            }.collectLatest { state ->
                submitState(state)
            }
        }
    }
}