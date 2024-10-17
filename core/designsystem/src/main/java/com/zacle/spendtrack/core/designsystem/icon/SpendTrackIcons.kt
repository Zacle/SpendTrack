package com.zacle.spendtrack.core.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector
import com.zacle.spendtrack.core.shared_resources.R

object SpendTrackIcons {
    val home = R.drawable.home
    val transaction = R.drawable.transaction
    val budget = R.drawable.budget
    val profile = R.drawable.profile
    val add = R.drawable.add
    val google = R.drawable.google
    val addExpense = R.drawable.add_expense
    val addIncome = R.drawable.add_income
    val camera = R.drawable.camera
    val image = R.drawable.image
    val warning = R.drawable.warning
    val filter = R.drawable.filter
    val report = R.drawable.report

    val visibility = Icons.Default.Visibility
    val visibilityOff = Icons.Default.VisibilityOff
    val arrowBack = Icons.AutoMirrored.Filled.ArrowBack
    val dropDown = Icons.Default.KeyboardArrowDown
    val notification = Icons.Default.Notifications
    val done = Icons.Default.Done
}

/**
 * A sealed class to make dealing with [ImageVector] and [DrawableRes] icons easier.
 */
sealed class Icon {
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}