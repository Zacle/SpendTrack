package com.zacle.spendtrack.core.domain.auth

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SignInWithEmailAndPasswordUseCase(
    configuration: Configuration,
    private val authenticationRepository: AuthenticationRepository
): UseCase<SignInWithEmailAndPasswordUseCase.Request, SignInWithEmailAndPasswordUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> {
        authenticationRepository.signInWithEmailAndPassword(request.email, request.password)
        return flowOf(Response)
    }

    data class Request(val email: String, val password: String) : UseCase.Request

    data object Response : UseCase.Response
}