package com.zacle.spendtrack.feature.profile.edit_profile

import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.model.User
import com.zacle.spendtrack.core.ui.types.FormError

data class ProfileUiState(
    val user: User? = null,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val isSaving: Boolean = false,
    val profileImage: ImageData? = null,
    val firstNameError: FormError? = null,
    val lastNameError: FormError? = null,
)