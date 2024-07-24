package com.zacle.spendtrack.core.data.repository

import com.zacle.spendtrack.core.datastore.UserPreferencesDataSource
import com.zacle.spendtrack.core.model.ThemeAppearance
import com.zacle.spendtrack.core.model.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class OfflineFirstUserDataRepositoryTest {
    private val userPreferencesDataSource = mock<UserPreferencesDataSource>()
    private val repository = OfflineFirstUserDataRepository(userPreferencesDataSource)

    @Test
    fun `should be able to retrieve user data`() = runTest {
        val expected = UserData(
            shouldHideOnboarding = false,
            themeAppearance = ThemeAppearance.FOLLOW_SYSTEM
        )
        whenever(userPreferencesDataSource.userData).thenReturn(flowOf(
            UserData(
                shouldHideOnboarding = false,
                themeAppearance = ThemeAppearance.FOLLOW_SYSTEM
            )
        ))
        val actual = repository.userData.first()
        assertEquals(expected, actual)
    }

    @Test
    fun `should be able to set should hide onboarding`() = runTest {
        repository.setShouldHideOnboarding(true)
        verify(userPreferencesDataSource).setShouldHideOnboarding(true)
    }
}