package com.zacle.spendtrack.core.domain.auth

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SignUpWithEmailAndPasswordUseCase(
    configuration: Configuration,
    private val authenticationRepository: AuthenticationRepository
): UseCase<SignUpWithEmailAndPasswordUseCase.Request, SignUpWithEmailAndPasswordUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> {
        val (firstName, lastName, email, password) = request
        val success = authenticationRepository.signUpWithEmailAndPassword(firstName, lastName, email, password)
        return flowOf(Response(success))
    }

    data class Request(
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String
    ): UseCase.Request

    data class Response(val success: Boolean): UseCase.Response

}