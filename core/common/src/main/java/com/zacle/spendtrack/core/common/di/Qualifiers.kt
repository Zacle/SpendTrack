package com.zacle.spendtrack.core.common.di

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Retention(RUNTIME)
@Qualifier
annotation class RemoteUserData

@Retention(RUNTIME)
@Qualifier
annotation class LocalUserData

@Retention(RUNTIME)
@Qualifier
annotation class RemoteBudgetData

@Retention(RUNTIME)
@Qualifier
annotation class LocalBudgetData

@Retention(RUNTIME)
@Qualifier
annotation class RemoteExpenseData

@Retention(RUNTIME)
@Qualifier
annotation class LocalExpenseData

@Retention(RUNTIME)
@Qualifier
annotation class RemoteIncomeData

@Retention(RUNTIME)
@Qualifier
annotation class LocalIncomeData

@Retention(RUNTIME)
@Qualifier
annotation class LocalCategoryData

@Retention(RUNTIME)
@Qualifier
annotation class RemoteCategoryData