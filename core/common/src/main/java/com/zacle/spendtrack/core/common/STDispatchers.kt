package com.zacle.spendtrack.core.common

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class STDispatcher(val dispatchers: STDispatchers)

enum class STDispatchers {
    Default,
    IO,
}