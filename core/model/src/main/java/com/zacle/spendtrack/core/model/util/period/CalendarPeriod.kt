package com.zacle.spendtrack.core.model.util.period

import com.zacle.spendtrack.core.model.Period
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

fun Instant.toDailyPeriod(): Period {
    val currentDay = convertInstantToOffsetDateTime(this)
    val start = currentDay
        .withHour(0)
        .withMinute(0)
        .withSecond(0).toInstant().toKotlinInstant()
    val end = currentDay
        .withHour(23)
        .withMinute(59).toInstant().toKotlinInstant()
    return Period(start, end)
}

fun Instant.toWeeklyPeriod(): Period {
    val currentDay = convertInstantToOffsetDateTime(this)
    val firstDayOfWeek: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    val startTime = currentDay
        .with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
        .withHour(0)
        .withMinute(0)
        .withSecond(1)
    val endTime = startTime
        .plusDays(6)
        .withHour(23)
        .withMinute(59)
        .withSecond(59)
    return Period(startTime.toInstant().toKotlinInstant(), endTime.toInstant().toKotlinInstant())
}

fun Instant.toMonthlyPeriod(): Period {
    val currentDay = convertInstantToOffsetDateTime(this)
    val currentDayMonthLength = LocalDate
        .of(currentDay.year, currentDay.month, currentDay.dayOfMonth)
        .lengthOfMonth()
    val start = currentDay
        .withDayOfMonth(1)
        .withHour(0)
        .withMinute(0)
        .withSecond(0).toInstant().toKotlinInstant()
    val end = currentDay
        .withDayOfMonth(currentDayMonthLength)
        .withHour(23)
        .withMinute(59).toInstant().toKotlinInstant()
    return Period(start, end)
}

fun Instant.toYearlyPeriod(): Period {
    val currentDay = convertInstantToOffsetDateTime(this)
    val start = currentDay
        .withMonth(1)
        .withDayOfMonth(1)
        .withHour(0)
        .withMinute(0).toInstant().toKotlinInstant()
    val end = currentDay
        .withMonth(12)
        .withDayOfMonth(31)
        .withHour(23)
        .withMinute(59).toInstant().toKotlinInstant()
    return Period(start, end)
}

private fun convertInstantToOffsetDateTime(instant: Instant): OffsetDateTime {
    val javaInstant = instant.toJavaInstant()
    val zoneId = ZoneId.systemDefault() // Get the system's default time zone
    val zoneOffset = zoneId.rules.getOffset(javaInstant) // Get the offset for the instant
    return javaInstant.atOffset(zoneOffset)
}