package com.zacle.spendtrack.core.common.di

import javax.inject.Qualifier

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class RemoteUserData

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class LocalUserData