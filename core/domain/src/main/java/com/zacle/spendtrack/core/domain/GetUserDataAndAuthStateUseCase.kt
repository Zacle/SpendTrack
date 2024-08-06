package com.zacle.spendtrack.core.domain

import com.zacle.spendtrack.core.domain.auth.ObserveUserAuthStateUseCase
import com.zacle.spendtrack.core.domain.datastore.GetUserDataUseCase
import com.zacle.spendtrack.core.model.UserData
import com.zacle.spendtrack.core.model.auth.AuthenticatedUserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetUserDataAndAuthStateUseCase(
    configuration: Configuration,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val observeUserAuthStateUseCase: ObserveUserAuthStateUseCase
): UseCase<GetUserDataAndAuthStateUseCase.Request, GetUserDataAndAuthStateUseCase.Response>(configuration) {

    override suspend fun process(request: Request): Flow<Response> =
        combine(
            getUserDataUseCase.process(GetUserDataUseCase.Request),
            observeUserAuthStateUseCase.process(ObserveUserAuthStateUseCase.Request)
        ) { userDataResponse, userInfoResponse ->
            Response(
                userData = userDataResponse.userData,
                userInfo = userInfoResponse.userInfo
            )
        }

    object Request: UseCase.Request

    data class Response(
        val userData: UserData,
        val userInfo: AuthenticatedUserInfo?
    ): UseCase.Response
}