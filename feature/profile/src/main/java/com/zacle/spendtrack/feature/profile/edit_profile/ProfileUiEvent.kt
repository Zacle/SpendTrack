package com.zacle.spendtrack.feature.profile.edit_profile

import com.zacle.spendtrack.core.ui.UiEvent

sealed class ProfileUiEvent: UiEvent {
    data class BlankNameError(val messageResId: Int): ProfileUiEvent()
    data class InvalidNameError(val messageResId: Int): ProfileUiEvent()
    data class ShortNameError(val messageResId: Int): ProfileUiEvent()
    data object NavigateToLogin: ProfileUiEvent()
}