package com.zacle.spendtrack.core.data.di

import android.content.Context
import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers
import com.zacle.spendtrack.core.common.di.LocalBudgetData
import com.zacle.spendtrack.core.common.di.LocalExpenseData
import com.zacle.spendtrack.core.common.di.LocalIncomeData
import com.zacle.spendtrack.core.common.di.LocalUserData
import com.zacle.spendtrack.core.common.di.RemoteBudgetData
import com.zacle.spendtrack.core.common.di.RemoteExpenseData
import com.zacle.spendtrack.core.common.di.RemoteIncomeData
import com.zacle.spendtrack.core.common.di.RemoteUserData
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.datasource.AuthStateUserDataSource
import com.zacle.spendtrack.core.data.datasource.AuthenticationDataSource
import com.zacle.spendtrack.core.data.datasource.BudgetDataSource
import com.zacle.spendtrack.core.data.datasource.CategoryDataSource
import com.zacle.spendtrack.core.data.datasource.DeletedBudgetDataSource
import com.zacle.spendtrack.core.data.datasource.DeletedExpenseDataSource
import com.zacle.spendtrack.core.data.datasource.DeletedIncomeDataSource
import com.zacle.spendtrack.core.data.datasource.ExpenseDataSource
import com.zacle.spendtrack.core.data.datasource.GoogleAuthDataSource
import com.zacle.spendtrack.core.data.datasource.IncomeDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableBudgetDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableExpenseDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableIncomeDataSource
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.data.repository.DefaultAuthStateUserRepository
import com.zacle.spendtrack.core.data.repository.DefaultAuthenticationRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstBudgetRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstCategoryRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstExpenseRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstIncomeRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstUserDataRepository
import com.zacle.spendtrack.core.datastore.UserPreferencesDataSource
import com.zacle.spendtrack.core.domain.repository.AuthStateUserRepository
import com.zacle.spendtrack.core.domain.repository.AuthenticationRepository
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.CategoryRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun  provideUserDataRepository(
        userPreferencesDataSource: UserPreferencesDataSource
    ): UserDataRepository = OfflineFirstUserDataRepository(userPreferencesDataSource)

    @Provides
    @Singleton
    fun provideAuthStateUserRepository(
        authStateUserDataSource: AuthStateUserDataSource
    ): AuthStateUserRepository = DefaultAuthStateUserRepository(authStateUserDataSource)

    @Provides
    @Singleton
    fun provideAuthenticationRepository(
        authenticationDataSource: AuthenticationDataSource,
        googleAuthDataSource: GoogleAuthDataSource,
        @RemoteUserData remoteUserDataSource: UserDataSource,
        @LocalUserData localUserDataSource: UserDataSource
    ): AuthenticationRepository =
        DefaultAuthenticationRepository(
            authenticationDataSource,
            googleAuthDataSource,
            remoteUserDataSource,
            localUserDataSource
        )

    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDataSource: CategoryDataSource
    ): CategoryRepository = OfflineFirstCategoryRepository(categoryDataSource)

    @Provides
    @Singleton
    fun provideBudgetRepository(
        @LocalBudgetData localBudgetDataSource: SyncableBudgetDataSource,
        @RemoteBudgetData remoteBudgetDataSource: BudgetDataSource,
        @STDispatcher(STDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context,
        networkMonitor: NetworkMonitor,
        deletedBudgetDataSource: DeletedBudgetDataSource
    ): BudgetRepository =
        OfflineFirstBudgetRepository(
            localBudgetDataSource = localBudgetDataSource,
            remoteBudgetDataSource = remoteBudgetDataSource,
            ioDispatcher = ioDispatcher,
            networkMonitor = networkMonitor,
            deletedBudgetDataSource = deletedBudgetDataSource,
            context = context
        )

    @Provides
    @Singleton
    fun provideExpenseRepository(
        @LocalExpenseData localExpenseDataSource: SyncableExpenseDataSource,
        @RemoteExpenseData remoteExpenseDataSource: ExpenseDataSource,
        @STDispatcher(STDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context,
        networkMonitor: NetworkMonitor,
        deletedExpenseDataSource: DeletedExpenseDataSource
    ): ExpenseRepository =
        OfflineFirstExpenseRepository(
            localExpenseDataSource = localExpenseDataSource,
            remoteExpenseDataSource = remoteExpenseDataSource,
            ioDispatcher = ioDispatcher,
            networkMonitor = networkMonitor,
            deletedExpenseDataSource = deletedExpenseDataSource,
            context = context
        )

    @Provides
    @Singleton
    fun provideIncomeRepository(
        @LocalIncomeData localIncomeDataSource: SyncableIncomeDataSource,
        @RemoteIncomeData remoteIncomeDataSource: IncomeDataSource,
        @STDispatcher(STDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context,
        networkMonitor: NetworkMonitor,
        deletedIncomeDataSource: DeletedIncomeDataSource
    ): IncomeRepository =
        OfflineFirstIncomeRepository(
            localIncomeDataSource = localIncomeDataSource,
            remoteIncomeDataSource = remoteIncomeDataSource,
            ioDispatcher = ioDispatcher,
            networkMonitor = networkMonitor,
            deletedIncomeDataSource = deletedIncomeDataSource,
            context = context
        )
}