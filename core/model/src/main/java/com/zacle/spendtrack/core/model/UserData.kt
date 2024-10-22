package com.zacle.spendtrack.core.model

data class UserData(
    val themeAppearance: ThemeAppearance,
    val shouldHideOnboarding: Boolean,
    val languageCode: String,
    val currencyCode: String
)
