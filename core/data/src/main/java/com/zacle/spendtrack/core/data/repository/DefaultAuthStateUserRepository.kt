package com.zacle.spendtrack.core.data.repository

import com.zacle.spendtrack.core.data.datasource.AuthStateUserDataSource
import com.zacle.spendtrack.core.domain.repository.AuthStateUserRepository
import com.zacle.spendtrack.core.model.auth.AuthenticatedUserInfo
import kotlinx.coroutines.flow.Flow

class DefaultAuthStateUserRepository(
    authStateUserDataSource: AuthStateUserDataSource
): AuthStateUserRepository {
    override val userInfo: Flow<AuthenticatedUserInfo?> = authStateUserDataSource.getUserInfo()
}