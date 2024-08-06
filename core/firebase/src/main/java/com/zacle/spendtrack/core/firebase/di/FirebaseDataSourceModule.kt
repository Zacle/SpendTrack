package com.zacle.spendtrack.core.firebase.di

import com.zacle.spendtrack.core.common.di.RemoteUserData
import com.zacle.spendtrack.core.data.datasource.AuthStateUserDataSource
import com.zacle.spendtrack.core.data.datasource.AuthenticationDataSource
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseAuthStateUserDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseAuthenticationDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseUserDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseDataSourceModule {
    @Binds
    abstract fun bindAuthStateUserDataSource(
        dataSource: FirebaseAuthStateUserDataSource
    ): AuthStateUserDataSource

    @Binds
    @RemoteUserData
    abstract fun bindUserDataSource(
        dataSource: FirebaseUserDataSource
    ): UserDataSource

    @Binds
    abstract fun bindAuthenticationDataSource(
        dataSource: FirebaseAuthenticationDataSource
    ): AuthenticationDataSource

}