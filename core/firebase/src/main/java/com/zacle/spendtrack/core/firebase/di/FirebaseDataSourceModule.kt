package com.zacle.spendtrack.core.firebase.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.zacle.spendtrack.core.common.di.ApplicationScope
import com.zacle.spendtrack.core.common.di.RemoteBudgetData
import com.zacle.spendtrack.core.common.di.RemoteExpenseData
import com.zacle.spendtrack.core.common.di.RemoteIncomeData
import com.zacle.spendtrack.core.common.di.RemoteUserData
import com.zacle.spendtrack.core.data.datasource.AuthStateUserDataSource
import com.zacle.spendtrack.core.data.datasource.AuthenticationDataSource
import com.zacle.spendtrack.core.data.datasource.BudgetDataSource
import com.zacle.spendtrack.core.data.datasource.ExpenseDataSource
import com.zacle.spendtrack.core.data.datasource.IncomeDataSource
import com.zacle.spendtrack.core.data.datasource.StorageDataSource
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseAuthStateUserDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseAuthenticationDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseBudgetDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseExpenseDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseIncomeDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseStorageDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseUserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseDataSourceModule {
    @Provides
    @Singleton
    fun provideAuthStateUserDataSource(
        auth: FirebaseAuth,
        @ApplicationScope scope: CoroutineScope
    ): AuthStateUserDataSource = FirebaseAuthStateUserDataSource(auth, scope)

    @Provides
    @Singleton
    @RemoteUserData
    fun provideUserDataSource(
        firestore: FirebaseFirestore
    ): UserDataSource = FirebaseUserDataSource(firestore)

    @Provides
    @Singleton
    fun provideAuthenticationDataSource(
        auth: FirebaseAuth
    ): AuthenticationDataSource = FirebaseAuthenticationDataSource(auth)

    @Provides
    @Singleton
    @RemoteBudgetData
    fun provideBudgetDataSource(
        firestore: FirebaseFirestore
    ): BudgetDataSource = FirebaseBudgetDataSource(firestore)

    @Provides
    @Singleton
    @RemoteExpenseData
    fun provideExpenseDataSource(
        firestore: FirebaseFirestore
    ): ExpenseDataSource = FirebaseExpenseDataSource(firestore)

    @Provides
    @Singleton
    @RemoteIncomeData
    fun provideIncomeDataSource(
        firestore: FirebaseFirestore
    ): IncomeDataSource = FirebaseIncomeDataSource(firestore)

    @Provides
    @Singleton
    fun provideFirebaseStorage(
        storage: FirebaseStorage
    ): StorageDataSource = FirebaseStorageDataSource(storage)
}