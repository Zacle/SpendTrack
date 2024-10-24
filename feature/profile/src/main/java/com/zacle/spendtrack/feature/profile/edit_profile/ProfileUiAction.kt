package com.zacle.spendtrack.feature.profile.edit_profile

import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.ui.UiAction

sealed class ProfileUiAction: UiAction {
    data class OnFirstNameChanged(val firstName: String): ProfileUiAction()
    data class OnLastNameChanged(val lastName: String): ProfileUiAction()
    data class OnProfileSelected(val profileImage: ImageData?): ProfileUiAction()
    data object OnSaveClicked: ProfileUiAction()
}