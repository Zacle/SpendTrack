package com.zacle.spendtrack.core.domain.datastore

import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import com.zacle.spendtrack.core.model.ThemeAppearance
import com.zacle.spendtrack.core.model.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class GetUserDataUseCaseTest {
    private val userDataRepository = mock<UserDataRepository>()
    private val useCase = GetUserDataUseCase(mock(), userDataRepository)

    @Test
    fun `should be able to retrieve user data`() = runTest {
        val request = GetUserDataUseCase.Request
        whenever(userDataRepository.userData).thenReturn(
            flowOf(
                UserData(
                    shouldHideOnboarding = false,
                    themeAppearance = ThemeAppearance.FOLLOW_SYSTEM
                )
            )
        )
        val response = useCase.process(request).first()
        assertEquals(
            GetUserDataUseCase.Response(
                UserData(shouldHideOnboarding = false, themeAppearance = ThemeAppearance.FOLLOW_SYSTEM)
            ),
            response
        )
    }
}