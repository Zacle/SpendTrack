package com.zacle.spendtrack

import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.datastore.GetUserDataUseCase
import com.zacle.spendtrack.core.model.UserData
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiEvent
import com.zacle.spendtrack.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val converter: MainActivityConverter
): BaseViewModel<UserData, UiState<UserData>, MainActivityUiAction, UiEvent>() {

    override fun initState(): UiState<UserData>  = UiState.Loading

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
            getUserDataUseCase.execute(GetUserDataUseCase.Request)
                .mapLatest {
                    converter.convert(it)
                }
                .collectLatest { state ->
                    submitState(state)
                }
        }
    }
}