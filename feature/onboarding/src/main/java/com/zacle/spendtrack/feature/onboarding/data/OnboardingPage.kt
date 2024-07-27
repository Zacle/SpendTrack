package com.zacle.spendtrack.feature.onboarding.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.zacle.spendtrack.feature.onboarding.R

/**
 * Onboarding data used to introduces the functionalities of SpendTrack App to first-time users
 */
sealed class OnboardingPage(
    @DrawableRes val imageResId: Int,
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int
) {
    data object FirstPage: OnboardingPage(
        imageResId = R.drawable.spentracker_illustration_2,
        titleResId = R.string.take_full_control,
        descriptionResId = R.string.take_full_control_description
    )

    data object SecondPage: OnboardingPage(
        imageResId = R.drawable.spentracker_illustration_1,
        titleResId = R.string.keep_tabs,
        descriptionResId = R.string.keep_tabs_description
    )

    data object ThirdPage: OnboardingPage(
        imageResId = R.drawable.spentracker_illustration_3,
        titleResId = R.string.be_proactive,
        descriptionResId = R.string.be_proactive_description
    )
}