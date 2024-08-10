package com.zacle.spendtrack.core.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector
import com.zacle.spendtrack.core.designsystem.R

object SpendTrackIcons {
    val home = R.drawable.home
    val transaction = R.drawable.transaction
    val budget = R.drawable.budget
    val profile = R.drawable.profile
    val add = R.drawable.add
    val google = R.drawable.google

    val visibility = Icons.Default.Visibility
    val visibilityOff = Icons.Default.VisibilityOff
    val arrowBack = Icons.AutoMirrored.Filled.ArrowBack
}

/**
 * A sealed class to make dealing with [ImageVector] and [DrawableRes] icons easier.
 */
sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}