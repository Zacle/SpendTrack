package com.zacle.spendtrack.core.domain.repository

import com.zacle.spendtrack.core.model.auth.AuthenticatedUserInfo
import kotlinx.coroutines.flow.Flow

/**
 * Listens to an Authentication state data source that emits updates on the current user.
 */
interface AuthStateUserRepository {
    /**
     * Returns an observable of the [AuthenticatedUserInfo]
     */
    val userInfo: Flow<AuthenticatedUserInfo?>
}