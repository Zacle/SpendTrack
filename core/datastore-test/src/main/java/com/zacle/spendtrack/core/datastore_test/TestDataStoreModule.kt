package com.zacle.spendtrack.core.datastore_test

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.zacle.spendtrack.core.common.di.ApplicationScope
import com.zacle.spendtrack.core.datastore.UserPreferencesSerializer
import com.zacle.spendtrack.core.datastore.di.DataStoreModule
import com.zacle.spendtrack.core.datastore_proto.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope
import org.junit.rules.TemporaryFolder
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class]
)
internal object TestDataStoreModule {
    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserPreferencesSerializer,
        tempFolder: TemporaryFolder
    ): DataStore<UserPreferences> =
        tempFolder.testUserPreferencesDataStore(scope, userPreferencesSerializer)

}

fun TemporaryFolder.testUserPreferencesDataStore(
    coroutineScope: CoroutineScope,
    userPreferencesSerializer: UserPreferencesSerializer = UserPreferencesSerializer(),
) = DataStoreFactory.create(
    serializer = userPreferencesSerializer,
    scope = coroutineScope,
) {
    newFile("user_preferences_test.pb")
}