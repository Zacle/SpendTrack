package com.zacle.spendtrack.feature.onboarding.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.zacle.spendtrack.feature.onboarding.R
import com.zacle.spendtrack.core.shared_resources.R as SharedR

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
        titleResId = SharedR.string.take_full_control,
        descriptionResId = SharedR.string.take_full_control_description
    )

    data object SecondPage: OnboardingPage(
        imageResId = R.drawable.spentracker_illustration_1,
        titleResId = SharedR.string.keep_tabs,
        descriptionResId = SharedR.string.keep_tabs_description
    )

    data object ThirdPage: OnboardingPage(
        imageResId = R.drawable.spentracker_illustration_3,
        titleResId = SharedR.string.be_proactive,
        descriptionResId = SharedR.string.be_proactive_description
    )
}