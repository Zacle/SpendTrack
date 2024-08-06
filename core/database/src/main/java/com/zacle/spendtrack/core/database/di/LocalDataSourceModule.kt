package com.zacle.spendtrack.core.database.di

import com.zacle.spendtrack.core.common.di.LocalUserData
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.database.dao.UserDao
import com.zacle.spendtrack.core.database.datasource.LocalUserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataSourceModule {
    @Provides
    @Singleton
    @LocalUserData
    fun provideLocalUserDataSource(userDao: UserDao): UserDataSource = LocalUserDataSource(userDao)

}