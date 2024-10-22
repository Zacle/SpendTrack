package com.zacle.spendtrack.core.domain.repository

import com.zacle.spendtrack.core.model.ThemeAppearance
import com.zacle.spendtrack.core.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>

    /**
     * Sets whether the user has completed the onboarding process.
     */
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)

    /**
     * Sets the preferred theme
     */
    suspend fun setThemeAppearance(themeAppearance: ThemeAppearance)

    /**
     * Sets the preferred language code
     */
    suspend fun setLanguageCode(languageCode: String)

    /**
     * Sets the preferred currency code
     */
    suspend fun setCurrencyCode(currencyCode: String)
}