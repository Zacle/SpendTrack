package com.zacle.spendtrack.core.ui.ext

import com.zacle.spendtrack.core.designsystem.component.convertInstantToLocalDateTime
import com.zacle.spendtrack.core.designsystem.component.dateToString
import kotlinx.datetime.Instant

fun formatDate(date: Instant): String {
    val dateTime = convertInstantToLocalDateTime(date)
    return dateToString(dateTime, givenFormat = "MMM yyyy")
}