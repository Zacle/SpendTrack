package com.zacle.spendtrack.core.common

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class SpendTrackDispatcher(val dispatchers: SpendTrackDispatchers)

enum class SpendTrackDispatchers {
    Default,
    IO,
}