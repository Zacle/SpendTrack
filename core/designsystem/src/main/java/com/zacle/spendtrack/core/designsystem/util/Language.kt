package com.zacle.spendtrack.core.designsystem.util

import android.content.Context
import com.zacle.spendtrack.core.shared_resources.R

data class Language(
    val code: String,
    val languageName: String,
    val flagResId: Int
)

fun getLanguages(context: Context) =
    listOf(
        Language("en", context.getString(R.string.english), R.drawable.usa),
        Language("es", context.getString(R.string.spanish), R.drawable.spain),
        Language("fr", context.getString(R.string.french), R.drawable.france),
        Language("de", context.getString(R.string.german), R.drawable.germany),
        Language("it", context.getString(R.string.italian), R.drawable.italy),
        Language("pt", context.getString(R.string.portuguese), R.drawable.portugal),
    )
