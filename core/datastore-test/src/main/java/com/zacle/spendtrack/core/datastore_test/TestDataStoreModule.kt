package com.zacle.spendtrack.core.datastore_test

import androidx.datastore.core.DataStoreFactory
import com.zacle.spendtrack.core.datastore.UserPreferencesSerializer
import kotlinx.coroutines.CoroutineScope
import org.junit.rules.TemporaryFolder


fun TemporaryFolder.testUserPreferencesDataStore(
    coroutineScope: CoroutineScope,
    userPreferencesSerializer: UserPreferencesSerializer = UserPreferencesSerializer(),
) = DataStoreFactory.create(
    serializer = userPreferencesSerializer,
    scope = coroutineScope,
) {
    newFile("user_preferences_test.pb")
}