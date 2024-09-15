package com.zacle.spendtrack.core.designsystem.component

import android.content.Context
import com.zacle.spendtrack.core.designsystem.R
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

fun formatLocalDateTime(context: Context, dateTime: LocalDateTime): String {
    val today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)
    val inputDate = dateTime.truncatedTo(ChronoUnit.DAYS)

    val startOfNextWeek = today.plusWeeks(1).with(TemporalAdjusters.previousOrSame(today.dayOfWeek))
    val endOfNextWeek = startOfNextWeek.plusDays(6)

    return when {
        inputDate.isEqual(today) -> context.getString(R.string.today)
        inputDate.isEqual(today.plusDays(1)) -> context.getString(R.string.tomorrow)
        inputDate.isEqual(today.minusDays(1)) -> context.getString(R.string.yesterday)
        inputDate.isAfter(startOfNextWeek) && inputDate.isBefore(endOfNextWeek.plusDays(1)) -> {
            "${context.getString(R.string.this_day)} ${inputDate.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}"
        }
        else -> {
            // Fallback to standard format: dd MMM yyyy
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
            inputDate.format(formatter)
        }
    }
}

fun convertInstantToLocalDateTime(instant: Instant): LocalDateTime {
    val javaInstant = instant.toJavaInstant()
    val zoneId = ZoneId.systemDefault() // Get the system's default time zone
    return LocalDateTime.ofInstant(javaInstant, zoneId)
}

fun dateToString(localDateTime: LocalDateTime, givenFormat: String = "hh:mm a"): String {
    val formatter = DateTimeFormatter.ofPattern(givenFormat)
    return localDateTime.format(formatter)
}