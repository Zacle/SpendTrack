package com.zacle.spendtrack.core.database.di

import android.content.Context
import androidx.room.Room
import com.zacle.spendtrack.core.database.STDatabase
import com.zacle.spendtrack.core.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun provideSTDatabase(
        @ApplicationContext context: Context,
    ): STDatabase = Room.databaseBuilder(
        context,
        STDatabase::class.java,
        "spendtrack-database"
    ).build()

    @Provides
    fun provideUserDao(
        database: STDatabase,
    ): UserDao = database.userDao()

}