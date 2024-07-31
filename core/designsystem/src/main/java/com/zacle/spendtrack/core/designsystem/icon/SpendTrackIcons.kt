package com.zacle.spendtrack.core.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.zacle.spendtrack.core.designsystem.R

object SpendTrackIcons {
    val home = R.drawable.home
    val transaction = R.drawable.transaction
    val budget = R.drawable.budget
    val profile = R.drawable.profile
    val add = R.drawable.add
}

/**
 * A sealed class to make dealing with [ImageVector] and [DrawableRes] icons easier.
 */
sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}