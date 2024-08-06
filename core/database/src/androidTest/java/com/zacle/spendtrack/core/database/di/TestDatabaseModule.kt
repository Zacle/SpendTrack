package com.zacle.spendtrack.core.database.di

import android.content.Context
import androidx.room.Room
import com.zacle.spendtrack.core.database.STDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
internal object TestDatabaseModule {
    @Provides
    @Named("test_db")
    fun provideSTDatabase(
        @ApplicationContext context: Context,
    ) = Room.inMemoryDatabaseBuilder(
        context,
        STDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()
}