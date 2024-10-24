package com.zacle.spendtrack.core.di

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.datastore.GetUserDataUseCase
import com.zacle.spendtrack.core.domain.datastore.SetCurrencyCodeUseCase
import com.zacle.spendtrack.core.domain.datastore.SetLanguageCodeUseCase
import com.zacle.spendtrack.core.domain.datastore.SetThemeAppearanceUseCase
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import com.zacle.spendtrack.core.domain.repository.UserRepository
import com.zacle.spendtrack.core.domain.user.GetUserUseCase
import com.zacle.spendtrack.core.domain.user.UpdateUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UserDataUseCaseModule {
    @Provides
    fun provideGetUserDataUseCase(
        configuration: UseCase.Configuration,
        userDataRepository: UserDataRepository
    ): GetUserDataUseCase = GetUserDataUseCase(configuration, userDataRepository)

    @Provides
    fun provideGetUserUseCase(
        configuration: UseCase.Configuration,
        userRepository: UserRepository
    ): GetUserUseCase = GetUserUseCase(configuration, userRepository)

    @Provides
    fun provideUpdateUserUseCase(
        configuration: UseCase.Configuration,
        userRepository: UserRepository
    ): UpdateUserUseCase = UpdateUserUseCase(configuration, userRepository)

    @Provides
    fun provideSetThemeAppearanceUseCase(
        configuration: UseCase.Configuration,
        userDataRepository: UserDataRepository
    ): SetThemeAppearanceUseCase = SetThemeAppearanceUseCase(configuration, userDataRepository)

    @Provides
    fun provideSetLanguageCodeUseCase(
        configuration: UseCase.Configuration,
        userDataRepository: UserDataRepository
    ): SetLanguageCodeUseCase = SetLanguageCodeUseCase(configuration, userDataRepository)

    @Provides
    fun provideSetCurrencyCodeUseCase(
        configuration: UseCase.Configuration,
        userDataRepository: UserDataRepository
    ): SetCurrencyCodeUseCase = SetCurrencyCodeUseCase(configuration, userDataRepository)
}