package com.zacle.spendtrack.core.data.repository

import com.zacle.spendtrack.core.common.di.LocalUserData
import com.zacle.spendtrack.core.common.di.RemoteUserData
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.domain.repository.AuthStateUserRepository
import com.zacle.spendtrack.core.domain.repository.UserRepository
import com.zacle.spendtrack.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OfflineFirstUserRepository @Inject constructor(
    @LocalUserData private val localUserDataSource: UserDataSource,
    @RemoteUserData private val remoteUserDataSource: UserDataSource,
    private val authStateUserRepository: AuthStateUserRepository,
    private val networkMonitor: NetworkMonitor
): UserRepository {
    override suspend fun getUser(): Flow<User?> =
        authStateUserRepository.userInfo.flatMapLatest { userInfo ->
            if (userInfo == null || !userInfo.isSignedIn()) {
                return@flatMapLatest flow { emit(null) }
            }
            val userId = userInfo.getUserId() ?: return@flatMapLatest flow { emit(null) }
            val isOnline = networkMonitor.isOnline.first()

            // Fetch from local data source first
            localUserDataSource.getUser(userId).flatMapLatest { localUser ->
                if (localUser == null && isOnline) {
                    // Fetch from remote if local is null and online
                    remoteUserDataSource.getUser(userId).flatMapLatest { remoteUser ->
                        if (remoteUser != null) {
                            // Save remote user to local data source
                            localUserDataSource.insertUser(remoteUser)
                        }
                        flow { emit(remoteUser) }
                    }
                } else {
                    // Emit the local user directly
                    flow { emit(localUser) }
                }
            }
        }

    override suspend fun updateUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun insertUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(userId: String) {
        TODO("Not yet implemented")
    }
}