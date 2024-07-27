package com.zacle.spendtrack.feature.onboarding.data

import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiEvent
import com.zacle.spendtrack.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): BaseViewModel<OnboardingPagesModel, UiState<OnboardingPagesModel>, OnboardingUiAction, UiEvent>() {

    override fun initState(): UiState<OnboardingPagesModel> = UiState.Loading

    init {
        submitAction(OnboardingUiAction.Load)
    }

    override fun handleAction(action: OnboardingUiAction) {
        when (action) {
            is OnboardingUiAction.Load -> {
                loadOnboardingPages()
            }
            is OnboardingUiAction.SetOnboarded -> {
                setUserOnboarded()
            }
        }
    }

    private fun setUserOnboarded() {
        viewModelScope.launch {
            userDataRepository.setShouldHideOnboarding(true)
        }
    }

    private fun loadOnboardingPages() {
        submitState(
            UiState.Success(
                OnboardingPagesModel(
                    pages = listOf(
                        OnboardingPage.FirstPage,
                        OnboardingPage.SecondPage,
                        OnboardingPage.ThirdPage
                    )
                )
            )
        )
    }
}