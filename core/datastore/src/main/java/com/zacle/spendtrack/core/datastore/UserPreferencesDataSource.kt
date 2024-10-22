package com.zacle.spendtrack.core.datastore

import androidx.datastore.core.DataStore
import com.zacle.spendtrack.core.datastore_proto.UserPreferences
import com.zacle.spendtrack.core.datastore_proto.copy
import com.zacle.spendtrack.core.model.ChangeLastSyncTimes
import com.zacle.spendtrack.core.model.ThemeAppearance
import com.zacle.spendtrack.core.model.UserData
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
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
                },
                languageCode = it.languageCode,
                currencyCode = it.currencyCode.ifEmpty { "USD" }
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

    suspend fun setLanguageCode(languageCode: String) {
        userPreferences.updateData {
            it.copy {
                this.languageCode = languageCode
            }
        }
    }

    suspend fun setCurrencyCode(currencyCode: String) {
        userPreferences.updateData {
            it.copy {
                this.currencyCode = currencyCode
            }
        }
    }

    suspend fun getChangeLastSyncTimes() = userPreferences.data
        .map {
            ChangeLastSyncTimes(
                userLastSync = it.userLastSync,
                expenseLastSync = it.expenseLastSync,
                incomeLastSync = it.incomeLastSync,
                billsLastSync = it.billsLastSync,
                budgetLastSync = it.budgetLastSync
            )
        }
        .firstOrNull() ?: ChangeLastSyncTimes()

    /**
     * Update the [ChangeLastSyncTimes] using the [update]
     */
    suspend fun updateChangeLastSyncTimes(update: ChangeLastSyncTimes.() -> ChangeLastSyncTimes) {
        try {
            userPreferences.updateData { currentPreferences ->
                val updatedChangeLastSyncTimes = update(
                    ChangeLastSyncTimes(
                        userLastSync = currentPreferences.userLastSync,
                        expenseLastSync = currentPreferences.expenseLastSync,
                        incomeLastSync = currentPreferences.incomeLastSync,
                        billsLastSync = currentPreferences.billsLastSync,
                        budgetLastSync = currentPreferences.budgetLastSync
                    )
                )
                currentPreferences.copy {
                    userLastSync = updatedChangeLastSyncTimes.userLastSync
                    expenseLastSync = updatedChangeLastSyncTimes.expenseLastSync
                    incomeLastSync = updatedChangeLastSyncTimes.incomeLastSync
                    billsLastSync = updatedChangeLastSyncTimes.billsLastSync
                    budgetLastSync = updatedChangeLastSyncTimes.budgetLastSync
                }
            }
        } catch (ioException: IOException) {
            Timber.e("Failed to update the last sync times")
        }
    }
}