package com.zacle.spendtrack.core.domain.datastore

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import com.zacle.spendtrack.core.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetUserDataUseCase(
    configuration: Configuration,
    private val userDataRepository: UserDataRepository
): UseCase<GetUserDataUseCase.Request, GetUserDataUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        userDataRepository.userData.map { Response(it) }

    object Request: UseCase.Request

    data class Response(val userData: UserData): UseCase.Response
}