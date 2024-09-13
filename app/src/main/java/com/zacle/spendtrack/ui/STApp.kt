package com.zacle.spendtrack.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.zacle.spendtrack.R
import com.zacle.spendtrack.core.designsystem.component.STNavigationBar
import com.zacle.spendtrack.core.designsystem.component.STNavigationBarItem
import com.zacle.spendtrack.core.designsystem.component.STNavigationDefaults
import com.zacle.spendtrack.core.designsystem.component.STNavigationSuiteScope
import com.zacle.spendtrack.core.designsystem.component.SpendTrackBackground
import com.zacle.spendtrack.data.UserStateModel
import com.zacle.spendtrack.feature.home.Home
import com.zacle.spendtrack.feature.login.Login
import com.zacle.spendtrack.feature.onboarding.navigation.Onboarding
import com.zacle.spendtrack.feature.verify_auth.VerifyAuth
import com.zacle.spendtrack.navigation.STNavHost
import com.zacle.spendtrack.navigation.TopLevelDestination

@Composable
fun STApp(
    appState: STAppState,
    userStateModel: UserStateModel,
    modifier: Modifier = Modifier
) {
    SpendTrackBackground(modifier = modifier) {
        val snackbarHostState = remember { SnackbarHostState() }
        val isOffline by appState.isOffline.collectAsStateWithLifecycle()

        // If user is not connected to the internet show a snack bar to inform them
        val notConnectedMessage = stringResource(R.string.not_connected)
        LaunchedEffect(isOffline) {
            if (isOffline) {
                snackbarHostState.showSnackbar(
                    message = notConnectedMessage,
                    duration = Indefinite
                )
            }
        }
        val startDestination: Any = getStartDestination(userStateModel)

        STApp(
            appState = appState,
            startDestination = startDestination,
            isOffline = isOffline,
            snackbarHostState = snackbarHostState
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun STApp(
    appState: STAppState,
    startDestination: Any,
    isOffline: Boolean,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
) {
    val currentDestination = appState.currentDestination
    val currentTopLevelDestination = appState.currentTopLevelDestination

    STNavigationSuiteScaffoldLayout(
        topLevelDestinations = appState.topLevelDestinations,
        currentDestination = currentDestination,
        currentTopLevelDestination = currentTopLevelDestination,
        onNavigateToTopLevelDestination = appState::navigateToTopLevelDestination,
        navigationSuiteItems = {
            if (currentTopLevelDestination != null) {
                appState.topLevelDestinations.forEach { destination ->
                    val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
                    item(
                        selected = selected,
                        onClick = { appState.navigateToTopLevelDestination(destination) },
                        icon = {
                            val tintColor =
                                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            Icon(
                                painter = painterResource(id = destination.icon.id),
                                contentDescription = null,
                                tint = tintColor
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(destination.titleTextId),
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
        },
        windowAdaptiveInfo = windowAdaptiveInfo
    ) {
        Scaffold(
            modifier = modifier.semantics {
                testTagsAsResourceId = true
            },
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            STNavHost(
                isOffline = isOffline,
                appState = appState,
                startDestination = startDestination
            )
        }
    }
}

private fun getStartDestination(userStateModel: UserStateModel): Any {
    val userInfo = userStateModel.userInfo
    val userData = userStateModel.userData

    return if (!userData.shouldHideOnboarding) {
        Onboarding
    } else if (userInfo == null || !userInfo.isSignedIn()) {
        Login
    } else if (userInfo.isEmailVerified() == false) {
        VerifyAuth
    } else {
        Home
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.substringAfterLast(".")?.contains(destination.name, ignoreCase = true) == true
    } == true

/**
 * SpendTrack navigation suite scaffold with item and content slots.
 * Wraps Material 3 [NavigationSuiteScaffoldLayout].
 *
 * @param topLevelDestinations The list of top level destinations.
 * @param currentDestination The current destination.
 * @param currentTopLevelDestination The current top level destination.
 * @param navigationSuiteItems A slot to display multiple items via [STNavigationSuiteScope].
 * @param windowAdaptiveInfo The window adaptive info.
 * @param content The app content inside the scaffold.
 */
@Composable
fun STNavigationSuiteScaffoldLayout(
    topLevelDestinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    currentTopLevelDestination: TopLevelDestination?,
    onNavigateToTopLevelDestination: (TopLevelDestination) -> Unit,
    navigationSuiteItems: STNavigationSuiteScope.() -> Unit,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    content: @Composable () -> Unit
) {
    val layoutType = NavigationSuiteScaffoldDefaults
        .calculateFromAdaptiveInfo(windowAdaptiveInfo)
    val navigationSuiteItemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = STNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = STNavigationDefaults.navigationContentColor(),
            selectedTextColor = STNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = STNavigationDefaults.navigationContentColor(),
            indicatorColor = STNavigationDefaults.navigationIndicatorColor(),
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = STNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = STNavigationDefaults.navigationContentColor(),
            selectedTextColor = STNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = STNavigationDefaults.navigationContentColor(),
            indicatorColor = STNavigationDefaults.navigationIndicatorColor(),
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = STNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = STNavigationDefaults.navigationContentColor(),
            selectedTextColor = STNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = STNavigationDefaults.navigationContentColor(),
        )
    )

    NavigationSuiteScaffoldLayout(
        navigationSuite = {
            if (layoutType == NavigationSuiteType.NavigationBar) {
                if (currentTopLevelDestination != null) {
                    STNavigationBar {
                        topLevelDestinations.forEach { destination ->
                            val selected =
                                currentDestination.isTopLevelDestinationInHierarchy(destination)
                            STNavigationBarItem(
                                selected = selected,
                                onClick = { onNavigateToTopLevelDestination(destination) },
                                icon = {
                                    val tintColor =
                                        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    Icon(
                                        painter = painterResource(id = destination.icon.id),
                                        contentDescription = null,
                                        tint = tintColor
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(destination.titleTextId),
                                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )

                        }
                    }
                }
            } else {
                if (currentTopLevelDestination != null) {
                    NavigationSuite(
                        layoutType = layoutType,
                        colors = NavigationSuiteDefaults.colors(
                            navigationBarContentColor = STNavigationDefaults.navigationContentColor(),
                            navigationRailContainerColor = Color.Transparent,
                        )
                    ) {
                        STNavigationSuiteScope(
                            navigationSuiteScope = this,
                            navigationSuiteItemColors = navigationSuiteItemColors,
                        ).run(navigationSuiteItems)
                    }
                }
            }
        }
    ) {
        content()
    }
}