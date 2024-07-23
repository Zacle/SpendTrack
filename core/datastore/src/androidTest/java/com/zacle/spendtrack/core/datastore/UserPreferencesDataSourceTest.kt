package com.zacle.spendtrack.core.datastore

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zacle.spendtrack.core.model.ThemeAppearance
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UserPreferencesDataSourceTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var subject: UserPreferencesDataSource

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun cleanup() {
        File(context.filesDir, "datastore").deleteRecursively()
    }

    @Test
    fun shouldHideOnboardingIsFalseByDefault() = runTest {
        assertFalse(subject.userData.first().shouldHideOnboarding)
    }

    @Test
    fun shouldHideOnboardingIsTrue() = runTest {
        subject.setShouldHideOnboarding(true)
        assertTrue(subject.userData.first().shouldHideOnboarding)
    }

    @Test
    fun themeAppearanceIsFollowSystemByDefault() = runTest {
        assertEquals(ThemeAppearance.FOLLOW_SYSTEM, subject.userData.first().themeAppearance)
    }

    @Test
    fun themeAppearanceIsLight() = runTest {
        subject.setThemeAppearance(ThemeAppearance.LIGHT)
        assertEquals(ThemeAppearance.LIGHT, subject.userData.first().themeAppearance)
    }

    @Test
    fun themeAppearanceIsDark() = runTest {
        subject.setThemeAppearance(ThemeAppearance.DARK)
        assertEquals(ThemeAppearance.DARK, subject.userData.first().themeAppearance)
    }
}