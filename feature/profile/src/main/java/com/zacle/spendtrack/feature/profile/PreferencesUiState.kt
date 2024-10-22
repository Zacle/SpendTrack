package com.zacle.spendtrack.feature.profile

import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.model.ThemeAppearance

data class PreferencesUiState(
    val name: String = "",
    val languageCode: String = "",
    val currencyCode: String = "",
    val photoImage: ImageData? = null,
    val themeAppearance: ThemeAppearance = ThemeAppearance.FOLLOW_SYSTEM,
    val isLanguageDialogOpen: Boolean = false,
    val isCurrencyDialogOpen: Boolean = false,
    val isThemeDialogOpen: Boolean = false
)
