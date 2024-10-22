package com.zacle.spendtrack.core.domain.datastore

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import com.zacle.spendtrack.core.model.ThemeAppearance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SetThemeAppearanceUseCase(
    configuration: Configuration,
    private val userDataRepository: UserDataRepository
): UseCase<SetThemeAppearanceUseCase.Request, SetThemeAppearanceUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> {
        userDataRepository.setThemeAppearance(request.themeAppearance)
        return flowOf(Response)
    }

    data class Request(val themeAppearance: ThemeAppearance): UseCase.Request

    data object Response: UseCase.Response
}