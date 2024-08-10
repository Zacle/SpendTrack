package com.zacle.spendtrack.core.domain.auth

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.AuthenticationRepository
import com.zacle.spendtrack.core.model.auth.AuthResult
import com.zacle.spendtrack.core.model.usecase.UseCaseException.NotAuthenticatedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SignInWithEmailAndPasswordUseCase(
    configuration: Configuration,
    private val authenticationRepository: AuthenticationRepository
): UseCase<SignInWithEmailAndPasswordUseCase.Request, SignInWithEmailAndPasswordUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> = flow {
        val (email, password) = request
        val authResult = authenticationRepository.signInWithEmailAndPassword(email, password)
        if (authResult.uid == null) throw NotAuthenticatedException(Throwable())
        emit(Response(authResult))
    }

    data class Request(val email: String, val password: String) : UseCase.Request

    data class Response(val authResult: AuthResult) : UseCase.Response
}