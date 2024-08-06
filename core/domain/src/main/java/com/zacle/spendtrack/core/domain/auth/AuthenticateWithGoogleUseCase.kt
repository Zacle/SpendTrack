package com.zacle.spendtrack.core.domain.auth

import android.content.Context
import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthenticateWithGoogleUseCase(
    configuration: Configuration,
    private val authenticationRepository: AuthenticationRepository
): UseCase<AuthenticateWithGoogleUseCase.Request, AuthenticateWithGoogleUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        authenticationRepository.authenticateWithGoogle(request.context)
            .map { Response(it) }

    data class Request(val context: Context) : UseCase.Request

    data class Response(val success: Boolean) : UseCase.Response
}