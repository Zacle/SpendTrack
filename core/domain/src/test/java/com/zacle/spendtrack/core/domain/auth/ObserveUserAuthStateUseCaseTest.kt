package com.zacle.spendtrack.core.domain.auth

import android.net.Uri
import com.zacle.spendtrack.core.domain.repository.AuthStateUserRepository
import com.zacle.spendtrack.core.model.auth.AuthenticatedUserInfo
import com.zacle.spendtrack.core.model.usecase.UseCaseException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObserveUserAuthStateUseCaseTest {
    private val authStateUserRepository = mock<AuthStateUserRepository>()
    private val useCase = ObserveUserAuthStateUseCase(mock(), authStateUserRepository)

    @Test
    fun `should return Response when user is authenticated and email is verified`() = runTest {
        val request = ObserveUserAuthStateUseCase.Request
        whenever(authStateUserRepository.userInfo).thenReturn(
            flowOf(
                FakeAuthenticatedUserInfo(
                    isSignedIn = true,
                    isEmailVerified = true,
                    userId = "userId"
                )
            )
        )
        val response = useCase.process(request).first()
        assertEquals(
            ObserveUserAuthStateUseCase.Response(
                FakeAuthenticatedUserInfo(
                    isSignedIn = true,
                    isEmailVerified = true,
                    userId = "userId"
                )
            ),
            response
        )
    }
}

class FakeAuthenticatedUserInfo(
    private val isSignedIn: Boolean = false,
    private val userId: String? = null,
    private val displayName: String? = null,
    private val photoUrl: Uri? = null,
    private val email: String? = null,
    private val phoneNumber: String? = null,
    private val lastSignInTimestamp: Long? = null,
    private val creationTimestamp: Long? = null,
    private val isEmailVerified: Boolean? = null
): AuthenticatedUserInfo {
    override fun isSignedIn(): Boolean = isSignedIn

    override fun getUserId(): String? = userId

    override fun getDisplayName(): String? = displayName

    override fun getPhotoUrl(): Uri? = photoUrl

    override fun getEmail(): String? = email

    override fun getPhoneNumber(): String? = phoneNumber

    override fun getLastSignInTimestamp(): Long? = lastSignInTimestamp

    override fun getCreationTimestamp(): Long? = creationTimestamp

    override fun isEmailVerified(): Boolean? = isEmailVerified

    override fun equals(other: Any?): Boolean {
        return this.userId == (other as FakeAuthenticatedUserInfo).userId
    }

    override fun hashCode(): Int {
        var result = isSignedIn.hashCode()
        result = 31 * result + (userId?.hashCode() ?: 0)
        result = 31 * result + (displayName?.hashCode() ?: 0)
        result = 31 * result + (photoUrl?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (phoneNumber?.hashCode() ?: 0)
        result = 31 * result + (lastSignInTimestamp?.hashCode() ?: 0)
        result = 31 * result + (creationTimestamp?.hashCode() ?: 0)
        result = 31 * result + (isEmailVerified?.hashCode() ?: 0)
        return result
    }
}