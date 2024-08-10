package com.zacle.spendtrack.core.di

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.auth.AuthenticateWithGoogleUseCase
import com.zacle.spendtrack.core.domain.auth.ForgotPasswordUseCase
import com.zacle.spendtrack.core.domain.auth.ObserveUserAuthStateUseCase
import com.zacle.spendtrack.core.domain.auth.SignInWithEmailAndPasswordUseCase
import com.zacle.spendtrack.core.domain.auth.SignOutUseCase
import com.zacle.spendtrack.core.domain.auth.SignUpWithEmailAndPasswordUseCase
import com.zacle.spendtrack.core.domain.repository.AuthStateUserRepository
import com.zacle.spendtrack.core.domain.repository.AuthenticationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthUseCaseModule {
    @Provides
    fun provideObserveUserAuthStateUseCase(
        configuration: UseCase.Configuration,
        authStateUserRepository: AuthStateUserRepository
    ): ObserveUserAuthStateUseCase = ObserveUserAuthStateUseCase(configuration, authStateUserRepository)

    @Provides
    fun provideAuthenticateWithGoogleUseCase(
        configuration: UseCase.Configuration,
        authenticationRepository: AuthenticationRepository
    ): AuthenticateWithGoogleUseCase = AuthenticateWithGoogleUseCase(configuration, authenticationRepository)

    @Provides
    fun provideSignInWithEmailAndPasswordUseCase(
        configuration: UseCase.Configuration,
        authenticationRepository: AuthenticationRepository
    ): SignInWithEmailAndPasswordUseCase = SignInWithEmailAndPasswordUseCase(configuration, authenticationRepository)

    @Provides
    fun provideSignUpWithEmailAndPasswordUseCase(
        configuration: UseCase.Configuration,
        authenticationRepository: AuthenticationRepository
    ): SignUpWithEmailAndPasswordUseCase = SignUpWithEmailAndPasswordUseCase(configuration, authenticationRepository)

    @Provides
    fun provideSignOutUseCase(
        configuration: UseCase.Configuration,
        authenticationRepository: AuthenticationRepository
    ): SignOutUseCase = SignOutUseCase(configuration, authenticationRepository)

    @Provides
    fun provideForgotPasswordUseCase(
        configuration: UseCase.Configuration,
        authenticationRepository: AuthenticationRepository
    ): ForgotPasswordUseCase = ForgotPasswordUseCase(configuration, authenticationRepository)
}