package com.zacle.spendtrack.feature.profile

import com.zacle.spendtrack.core.model.ThemeAppearance
import com.zacle.spendtrack.core.ui.UiAction

sealed class PreferencesUiAction: UiAction {
    data object OnLanguagePressed : PreferencesUiAction()
    data object OnLanguageDismissed : PreferencesUiAction()
    data class OnLanguageConfirmed(val languageCode: String) : PreferencesUiAction()
    data object OnCurrencyPressed : PreferencesUiAction()
    data object OnCurrencyDismissed : PreferencesUiAction()
    data class OnCurrencyConfirmed(val currencyCode: String) : PreferencesUiAction()
    data object OnThemePressed : PreferencesUiAction()
    data object OnThemeDismissed : PreferencesUiAction()
    data class OnThemeAppearanceConfirmed(val themeAppearance: ThemeAppearance) : PreferencesUiAction()
    data object OnLogoutPressed: PreferencesUiAction()
}