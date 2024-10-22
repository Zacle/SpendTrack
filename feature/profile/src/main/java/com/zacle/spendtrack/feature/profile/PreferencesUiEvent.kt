package com.zacle.spendtrack.feature.profile

import com.zacle.spendtrack.core.ui.UiEvent

sealed class PreferencesUiEvent: UiEvent {
    data object NavigateToLogin: PreferencesUiEvent()
}