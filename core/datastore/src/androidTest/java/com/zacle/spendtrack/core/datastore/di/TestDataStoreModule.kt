package com.zacle.spendtrack.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.zacle.spendtrack.core.common.di.ApplicationScope
import com.zacle.spendtrack.core.datastore.UserPreferencesSerializer
import com.zacle.spendtrack.core.datastore_proto.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton
import kotlin.random.Random

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class]
)
object TestDataStoreModule {
    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserPreferencesSerializer
    ): DataStore<UserPreferences> {
        val random = Random.nextInt()
        return DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = scope
        ) {
            context.dataStoreFile("test_user_preferences-$random.pb")
        }
    }
}