package com.zacle.spendtrack.core.database.datasource

import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.database.dao.UserDao
import com.zacle.spendtrack.core.database.model.asEntity
import com.zacle.spendtrack.core.database.model.asExternalModel
import com.zacle.spendtrack.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalUserDataSource @Inject constructor(
    private val userDao: UserDao
): UserDataSource {
    override suspend fun getUser(userId: String): Flow<User?> =
        userDao.getUser(userId).map { it?.asExternalModel() }

    override suspend fun updateUser(user: User) = userDao.updateUser(user.asEntity())

    override suspend fun insertUser(user: User) = userDao.insertUser(user.asEntity())

    override suspend fun deleteUser(userId: String) = userDao.deleteUser(userId)
}