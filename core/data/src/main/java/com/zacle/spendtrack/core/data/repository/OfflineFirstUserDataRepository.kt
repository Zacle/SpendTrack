package com.zacle.spendtrack.core.data.repository

import com.zacle.spendtrack.core.datastore.UserPreferencesDataSource
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import com.zacle.spendtrack.core.model.UserData
import kotlinx.coroutines.flow.Flow

class OfflineFirstUserDataRepository (
    private val userPreferencesDataSource: UserPreferencesDataSource
): UserDataRepository {
    override val userData: Flow<UserData>
        get() = userPreferencesDataSource.userData

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
    }
}