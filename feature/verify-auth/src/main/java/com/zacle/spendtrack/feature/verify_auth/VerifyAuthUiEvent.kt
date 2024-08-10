package com.zacle.spendtrack.feature.verify_auth

import com.zacle.spendtrack.core.ui.UiEvent

sealed class VerifyAuthUiEvent: UiEvent {
    data object NavigateToHome: VerifyAuthUiEvent()
    data object NotVerifiedYet: VerifyAuthUiEvent()
}