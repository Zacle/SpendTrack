package com.zacle.spendtrack.core.domain.auth

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ForgotPasswordUseCase(
    configuration: Configuration,
    private val authenticationRepository: AuthenticationRepository
): UseCase<ForgotPasswordUseCase.Request, ForgotPasswordUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> = flow {
        val success = authenticationRepository.sendPasswordResetEmail(request.email)
        emit(Response(success))
    }

    data class Request(val email: String) : UseCase.Request

    data class Response(val success: Boolean) : UseCase.Response
}