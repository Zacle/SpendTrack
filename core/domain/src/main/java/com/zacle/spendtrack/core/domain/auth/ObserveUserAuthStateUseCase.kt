package com.zacle.spendtrack.core.domain.auth

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.AuthStateUserRepository
import com.zacle.spendtrack.core.model.auth.AuthenticatedUserInfo
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A [UseCase] that observes the authentication state of the user.
 *
 * @return A [Flow] of [Response] objects representing the user's authentication state.
 *
 * @throws UseCaseException.NotAuthenticatedException if the user is not authenticated.
 * @throws UseCaseException.EmailNotVerifiedException if the user's email is not verified.
 */
class ObserveUserAuthStateUseCase(
    configuration: Configuration,
    private val authStateUserRepository: AuthStateUserRepository
): UseCase<ObserveUserAuthStateUseCase.Request, ObserveUserAuthStateUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        authStateUserRepository.userInfo.map { userAuthState ->
            if (userAuthState == null || !userAuthState.isSignedIn())
                throw UseCaseException.NotAuthenticatedException(Throwable())
            else if (userAuthState.isEmailVerified() == false)
                throw UseCaseException.EmailNotVerifiedException(Throwable())
            else Response(userAuthState)
        }

    object Request: UseCase.Request

    data class Response(val userInfo: AuthenticatedUserInfo): UseCase.Response
}