package com.zacle.spendtrack.core.domain.auth

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SignOutUseCase(
    configuration: Configuration,
    private val authenticationRepository: AuthenticationRepository
): UseCase<SignOutUseCase.Request, SignOutUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> {
        authenticationRepository.signOut()
        return flowOf(Response)
    }

    data object Request : UseCase.Request

    data object Response : UseCase.Response
}