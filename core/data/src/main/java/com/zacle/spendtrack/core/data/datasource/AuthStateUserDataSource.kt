package com.zacle.spendtrack.core.data.datasource

import com.zacle.spendtrack.core.model.auth.AuthenticatedUserInfo
import kotlinx.coroutines.flow.Flow

/**
 * Listens to an Authentication state data source that emits updates on the current user.
 */
interface AuthStateUserDataSource {
    /**
     * Returns an observable of the [AuthenticatedUserInfo]
     */
    fun getUserInfo(): Flow<AuthenticatedUserInfo?>
}