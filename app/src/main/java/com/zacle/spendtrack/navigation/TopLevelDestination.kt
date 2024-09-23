package com.zacle.spendtrack.navigation

import com.zacle.spendtrack.core.designsystem.icon.Icon
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.shared_resources.R

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
    val icon: Icon.DrawableResourceIcon,
    val titleTextId: Int
) {
    HOME(
        icon = Icon.DrawableResourceIcon(SpendTrackIcons.home),
        titleTextId = R.string.home
    ),
    TRANSACTION(
        icon = Icon.DrawableResourceIcon(SpendTrackIcons.transaction),
        titleTextId = R.string.transaction
    ),
    BUDGET(
        icon = Icon.DrawableResourceIcon(SpendTrackIcons.budget),
        titleTextId = R.string.budget
    ),
    PROFILE(
        icon = Icon.DrawableResourceIcon(SpendTrackIcons.profile),
        titleTextId = R.string.profile
    )
}