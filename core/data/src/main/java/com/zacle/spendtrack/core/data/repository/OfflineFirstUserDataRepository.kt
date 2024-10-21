package com.zacle.spendtrack.core.data.repository

import com.zacle.spendtrack.core.datastore.UserPreferencesDataSource
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import com.zacle.spendtrack.core.model.ThemeAppearance
import com.zacle.spendtrack.core.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineFirstUserDataRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource
): UserDataRepository {
    override val userData: Flow<UserData>
        get() = userPreferencesDataSource.userData

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
    }

    override suspend fun setThemeAppearance(themeAppearance: ThemeAppearance) {
        userPreferencesDataSource.setThemeAppearance(themeAppearance)
    }

    override suspend fun setLanguageCode(languageCode: String) {
        userPreferencesDataSource.setLanguageCode(languageCode)
    }

    override suspend fun setCurrencyCode(currencyCode: String) {
        userPreferencesDataSource.setCurrencyCode(currencyCode)
    }
}