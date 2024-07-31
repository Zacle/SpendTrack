package com.zacle.spendtrack.navigation

import com.zacle.spendtrack.R
import com.zacle.spendtrack.core.designsystem.icon.Icon
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.feature.home.R as homeR
import com.zacle.spendtrack.feature.transaction.R as transactionR
import com.zacle.spendtrack.feature.budget.R as budgetR
import com.zacle.spendtrack.feature.profile.R as profileR

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
        titleTextId = homeR.string.home
    ),
    TRANSACTION(
        icon = Icon.DrawableResourceIcon(SpendTrackIcons.transaction),
        titleTextId = transactionR.string.transaction
    ),
    BUDGET(
        icon = Icon.DrawableResourceIcon(SpendTrackIcons.budget),
        titleTextId = budgetR.string.budget
    ),
    PROFILE(
        icon = Icon.DrawableResourceIcon(SpendTrackIcons.profile),
        titleTextId = profileR.string.profile
    )
}