package com.zacle.spendtrack.feature.onboarding

import com.zacle.spendtrack.core.ui.UiAction

sealed class OnboardingUiAction: UiAction {
    data object Load: OnboardingUiAction()
    data object SetOnboarded: OnboardingUiAction()
}