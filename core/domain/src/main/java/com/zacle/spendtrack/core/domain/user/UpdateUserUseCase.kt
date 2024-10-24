package com.zacle.spendtrack.core.domain.user

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.UserRepository
import com.zacle.spendtrack.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UpdateUserUseCase(
    configuration: Configuration,
    private val userRepository: UserRepository,
): UseCase<UpdateUserUseCase.Request, UpdateUserUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> {
        userRepository.updateUser(request.user)
        return flowOf(Response)
    }

    data class Request(val user: User): UseCase.Request

    data object Response: UseCase.Response
}