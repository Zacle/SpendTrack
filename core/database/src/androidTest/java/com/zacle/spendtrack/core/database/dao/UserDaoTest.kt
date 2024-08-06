package com.zacle.spendtrack.core.database.dao

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.zacle.spendtrack.core.database.STDatabase
import com.zacle.spendtrack.core.database.TestDispatcherRule
import com.zacle.spendtrack.core.database.model.UserEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@SmallTest
class UserDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @Inject
    @Named("test_db")
    lateinit var database: STDatabase

    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        hiltRule.inject()
        userDao = database.userDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun returnEmptyUser() = runTest {
        val user = userDao.getUser("1").first()
        assertThat(user).isNull()
    }

    @Test
    fun insertUser() = runTest {
        val user = user
        userDao.insertUser(user)
        val userFromDb = userDao.getUser(user.id).first()
        assertThat(userFromDb).isEqualTo(user)
    }

    @Test
    fun deleteUser() = runTest {
        val user = user
        userDao.insertUser(user)
        userDao.deleteUser(user.id)
        val userFromDb = userDao.getUser(user.id).first()
        assertThat(userFromDb).isNull()
    }
}

val user = UserEntity(
    id = "1",
    email = "email",
    createdAt = Clock.System.now()
)