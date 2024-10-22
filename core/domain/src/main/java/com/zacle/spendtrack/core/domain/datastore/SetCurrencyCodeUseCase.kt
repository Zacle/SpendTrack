package com.zacle.spendtrack.core.domain.datastore

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SetCurrencyCodeUseCase(
    configuration: Configuration,
    private val userDataRepository: UserDataRepository
): UseCase<SetCurrencyCodeUseCase.Request, SetCurrencyCodeUseCase.Response>(configuration) {
    override suspend fun process(request: Request): Flow<Response> {
        userDataRepository.setCurrencyCode(request.currencyCode)
        return flowOf(Response)
    }

    data class Request(val currencyCode: String): UseCase.Request

    data object Response: UseCase.Response
}