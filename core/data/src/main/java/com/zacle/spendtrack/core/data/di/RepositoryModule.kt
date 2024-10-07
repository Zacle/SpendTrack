package com.zacle.spendtrack.core.data.di

import android.content.Context
import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers
import com.zacle.spendtrack.core.common.di.LocalBudgetData
import com.zacle.spendtrack.core.common.di.LocalCategoryData
import com.zacle.spendtrack.core.common.di.LocalExpenseData
import com.zacle.spendtrack.core.common.di.LocalIncomeData
import com.zacle.spendtrack.core.common.di.LocalUserData
import com.zacle.spendtrack.core.common.di.RemoteBudgetData
import com.zacle.spendtrack.core.common.di.RemoteCategoryData
import com.zacle.spendtrack.core.common.di.RemoteExpenseData
import com.zacle.spendtrack.core.common.di.RemoteIncomeData
import com.zacle.spendtrack.core.common.di.RemoteUserData
import com.zacle.spendtrack.core.common.util.ImageStorageManager
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
import com.zacle.spendtrack.core.data.datasource.StorageDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableBudgetDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableExpenseDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableIncomeDataSource
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.data.notification.BudgetAlertNotifier
import com.zacle.spendtrack.core.data.repository.DefaultAuthStateUserRepository
import com.zacle.spendtrack.core.data.repository.DefaultAuthenticationRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstBudgetRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstCategoryRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstExpenseRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstIncomeRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstUserDataRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstUserRepository
import com.zacle.spendtrack.core.datastore.UserPreferencesDataSource
import com.zacle.spendtrack.core.domain.repository.AuthStateUserRepository
import com.zacle.spendtrack.core.domain.repository.AuthenticationRepository
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.domain.repository.CategoryRepository
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import com.zacle.spendtrack.core.domain.repository.UserRepository
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
        @LocalCategoryData categoryDataSource: CategoryDataSource,
        @RemoteCategoryData remoteCategoryDataSource: CategoryDataSource,
        @STDispatcher(STDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        networkMonitor: NetworkMonitor
    ): CategoryRepository =
        OfflineFirstCategoryRepository(
            localCategoryDataSource = categoryDataSource,
            remoteCategoryDataSource = remoteCategoryDataSource,
            ioDispatcher = ioDispatcher,
            networkMonitor = networkMonitor
        )

    @Provides
    @Singleton
    fun provideBudgetRepository(
        @LocalBudgetData localBudgetDataSource: SyncableBudgetDataSource,
        @RemoteBudgetData remoteBudgetDataSource: BudgetDataSource,
        @STDispatcher(STDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context,
        networkMonitor: NetworkMonitor,
        deletedBudgetDataSource: DeletedBudgetDataSource,
        budgetAlarmNotifier: BudgetAlertNotifier
    ): BudgetRepository =
        OfflineFirstBudgetRepository(
            localBudgetDataSource = localBudgetDataSource,
            remoteBudgetDataSource = remoteBudgetDataSource,
            ioDispatcher = ioDispatcher,
            networkMonitor = networkMonitor,
            deletedBudgetDataSource = deletedBudgetDataSource,
            context = context,
            budgetAlarmNotifier = budgetAlarmNotifier
        )

    @Provides
    @Singleton
    fun provideExpenseRepository(
        @LocalExpenseData localExpenseDataSource: SyncableExpenseDataSource,
        @RemoteExpenseData remoteExpenseDataSource: ExpenseDataSource,
        @STDispatcher(STDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context,
        networkMonitor: NetworkMonitor,
        deletedExpenseDataSource: DeletedExpenseDataSource,
        storageDataSource: StorageDataSource,
        imageStorageManager: ImageStorageManager
    ): ExpenseRepository =
        OfflineFirstExpenseRepository(
            localExpenseDataSource = localExpenseDataSource,
            remoteExpenseDataSource = remoteExpenseDataSource,
            ioDispatcher = ioDispatcher,
            networkMonitor = networkMonitor,
            deletedExpenseDataSource = deletedExpenseDataSource,
            context = context,
            storageDataSource = storageDataSource,
            imageStorageManager = imageStorageManager
        )

    @Provides
    @Singleton
    fun provideIncomeRepository(
        @LocalIncomeData localIncomeDataSource: SyncableIncomeDataSource,
        @RemoteIncomeData remoteIncomeDataSource: IncomeDataSource,
        @STDispatcher(STDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context,
        networkMonitor: NetworkMonitor,
        deletedIncomeDataSource: DeletedIncomeDataSource,
        storageDataSource: StorageDataSource,
        imageStorageManager: ImageStorageManager
    ): IncomeRepository =
        OfflineFirstIncomeRepository(
            localIncomeDataSource = localIncomeDataSource,
            remoteIncomeDataSource = remoteIncomeDataSource,
            ioDispatcher = ioDispatcher,
            networkMonitor = networkMonitor,
            deletedIncomeDataSource = deletedIncomeDataSource,
            context = context,
            storageDataSource = storageDataSource,
            imageStorageManager = imageStorageManager
        )

    @Provides
    @Singleton
    fun provideUserRepository(
        @LocalUserData localUserDataSource: UserDataSource,
        @RemoteUserData remoteUserDataSource: UserDataSource,
        authStateUserRepository: AuthStateUserRepository,
        networkMonitor: NetworkMonitor
    ): UserRepository = OfflineFirstUserRepository(
        localUserDataSource = localUserDataSource,
        remoteUserDataSource = remoteUserDataSource,
        authStateUserRepository = authStateUserRepository,
        networkMonitor = networkMonitor
    )
}