package com.zacle.spendtrack.feature.verify_auth

import com.zacle.spendtrack.core.ui.UiAction

sealed class VerifyAuthUiAction: UiAction {
    data object OnAlreadyVerifiedClicked: VerifyAuthUiAction()
}