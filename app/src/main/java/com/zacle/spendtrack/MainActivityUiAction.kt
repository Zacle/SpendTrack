package com.zacle.spendtrack

import com.zacle.spendtrack.core.ui.UiAction

sealed class MainActivityUiAction: UiAction {
    data object Load: MainActivityUiAction()
}