package com.zacle.spendtrack.core.domain.datastore

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SetLanguageCodeUseCase(
    configuration: Configuration,
    private val userDataRepository: UserDataRepository
): UseCase<SetLanguageCodeUseCase.Request, SetLanguageCodeUseCase.Response>(configuration) {
    override suspend fun process(request: Request): Flow<Response> {
        userDataRepository.setLanguageCode(request.languageCode)
        return flowOf(Response)
    }

    data class Request(val languageCode: String): UseCase.Request

    data object Response: UseCase.Response
}