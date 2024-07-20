package com.zacle.spendtrack.core.datastore

import androidx.datastore.core.DataStore
import com.zacle.spendtrack.core.datastore_proto.UserPreferences
import com.zacle.spendtrack.core.datastore_proto.copy
import com.zacle.spendtrack.core.model.ThemeAppearance
import com.zacle.spendtrack.core.model.UserData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class UserPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val userData = userPreferences.data
        .map {
            UserData(
                shouldHideOnboarding = it.shouldHideOnboarding,
                themeAppearance = when (it.themeAppearance) {
                    null,
                    ThemeAppearanceProto.UNRECOGNIZED,
                    ThemeAppearanceProto.THEME_APPEARANCE_SYSTEM,
                    -> ThemeAppearance.FOLLOW_SYSTEM
                    ThemeAppearanceProto.THEME_APPEARANCE_LIGHT -> ThemeAppearance.LIGHT
                    ThemeAppearanceProto.THEME_APPEARANCE_DARK -> ThemeAppearance.DARK
                }
            )
        }

    suspend fun setThemeAppearance(themeAppearance: ThemeAppearance) {
        userPreferences.updateData {
            it.copy {
                this.themeAppearance = when (themeAppearance) {
                    ThemeAppearance.FOLLOW_SYSTEM -> ThemeAppearanceProto.THEME_APPEARANCE_SYSTEM
                    ThemeAppearance.LIGHT -> ThemeAppearanceProto.THEME_APPEARANCE_LIGHT
                    ThemeAppearance.DARK -> ThemeAppearanceProto.THEME_APPEARANCE_DARK
                }
            }
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy {
                this.shouldHideOnboarding = shouldHideOnboarding
            }
        }
    }
}
