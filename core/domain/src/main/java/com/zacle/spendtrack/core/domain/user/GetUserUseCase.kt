package com.zacle.spendtrack.core.domain.user

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.UserRepository
import com.zacle.spendtrack.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetUserUseCase(
    configuration: Configuration,
    private val userRepository: UserRepository
): UseCase<GetUserUseCase.Request, GetUserUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        userRepository.getUser().map { Response(it) }

    data object Request: UseCase.Request

    data class Response(val user: User?): UseCase.Response
}