package com.zacle.spendtrack.core.domain.auth

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.auth.ObserveUserAuthStateUseCase.Request
import com.zacle.spendtrack.core.domain.auth.ObserveUserAuthStateUseCase.Response
import com.zacle.spendtrack.core.domain.repository.AuthStateUserRepository
import com.zacle.spendtrack.core.model.auth.AuthenticatedUserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A [UseCase] that observes the authentication state of the user.
 *
 * @return A [Flow] of [Response] objects representing the user's authentication state.
 */
class ObserveUserAuthStateUseCase(
    configuration: Configuration,
    private val authStateUserRepository: AuthStateUserRepository
): UseCase<Request, Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        authStateUserRepository.userInfo.map { Response(it) }

    object Request: UseCase.Request

    data class Response(val userInfo: AuthenticatedUserInfo?): UseCase.Response
}