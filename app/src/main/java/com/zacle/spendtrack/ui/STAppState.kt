package com.zacle.spendtrack.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.common.util.TimeZoneMonitor
import com.zacle.spendtrack.feature.budget.budgets.Budgets
import com.zacle.spendtrack.feature.budget.budgets.navigateToBudgets
import com.zacle.spendtrack.feature.home.Home
import com.zacle.spendtrack.feature.home.navigateToHome
import com.zacle.spendtrack.feature.report.Report
import com.zacle.spendtrack.feature.report.navigateToReport
import com.zacle.spendtrack.feature.transaction.Transaction
import com.zacle.spendtrack.feature.transaction.navigateToTransaction
import com.zacle.spendtrack.navigation.TopLevelDestination
import com.zacle.spendtrack.navigation.TopLevelDestination.BUDGET
import com.zacle.spendtrack.navigation.TopLevelDestination.HOME
import com.zacle.spendtrack.navigation.TopLevelDestination.REPORT
import com.zacle.spendtrack.navigation.TopLevelDestination.TRANSACTION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone

@Composable
fun rememberSTAppState(
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
): STAppState {
    return remember(
        navController,
        coroutineScope,
        networkMonitor,
        timeZoneMonitor
    ) {
        STAppState(
            navController,
            coroutineScope,
            networkMonitor,
            timeZoneMonitor
        )
    }
}

@Stable
class STAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    timeZoneMonitor: TimeZoneMonitor
) {
    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route?.substringAfterLast(".")) {
            Home.toString() -> HOME
            Transaction.toString() -> TRANSACTION
            Budgets.toString() -> BUDGET
            Report.toString() -> REPORT
            else -> null
        }

    val currentTimeZone = timeZoneMonitor.currentTimeZone
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TimeZone.currentSystemDefault()
        )

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
        when (topLevelDestination) {
            HOME -> navController.navigateToHome(topLevelNavOptions)
            TRANSACTION -> navController.navigateToTransaction(topLevelNavOptions)
            BUDGET -> navController.navigateToBudgets(topLevelNavOptions)
            REPORT -> navController.navigateToReport(topLevelNavOptions)
        }
    }
}