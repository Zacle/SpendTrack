package com.zacle.spendtrack.core.firebase.di

import com.zacle.spendtrack.core.data.datasource.AuthStateUserDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseAuthStateUserDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseDataSourceModule {
    @Binds
    abstract fun bindAuthStateUserDataSource(
        dataSource: FirebaseAuthStateUserDataSource
    ): AuthStateUserDataSource
}