package com.zacle.spendtrack.feature.verify_auth

import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.auth.ObserveUserAuthStateUseCase
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyAuthViewModel @Inject constructor(
    private val observeUserAuthStateUseCase: ObserveUserAuthStateUseCase,
): BaseViewModel<Unit, UiState<Unit>, VerifyAuthUiAction, VerifyAuthUiEvent>() {

    override fun initState(): UiState<Unit> = UiState.Success(Unit)

    override fun handleAction(action: VerifyAuthUiAction) {
        when (action) {
            is VerifyAuthUiAction.OnAlreadyVerifiedClicked -> {
                onVerifyClicked()
            }
        }
    }

    private fun onVerifyClicked() {
        viewModelScope.launch {
            observeUserAuthStateUseCase.execute(ObserveUserAuthStateUseCase.Request)
                .collectLatest { response ->
                    if (response is Result.Success) {
                        val authInfo = response.data.userInfo
                        if (authInfo?.isEmailVerified() == true) {
                            submitSingleEvent(VerifyAuthUiEvent.NavigateToHome)
                        } else {
                            submitSingleEvent(VerifyAuthUiEvent.NotVerifiedYet)
                        }
                    } else {
                        submitSingleEvent(VerifyAuthUiEvent.NotVerifiedYet)
                    }
                }
        }
    }
}