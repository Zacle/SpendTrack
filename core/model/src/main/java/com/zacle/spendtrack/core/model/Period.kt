package com.zacle.spendtrack.core.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Period(
    val start: Instant = Clock.System.now(),
    val end: Instant = Clock.System.now()
)
