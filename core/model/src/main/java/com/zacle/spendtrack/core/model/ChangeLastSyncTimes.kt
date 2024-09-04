package com.zacle.spendtrack.core.model

/**
 * Class summarizing the local version of each model for sync
 */
data class ChangeLastSyncTimes(
    val userLastSync: Long = -1,
    val expenseLastSync: Long = -1,
    val incomeLastSync: Long = -1,
    val billsLastSync: Long = -1,
    val budgetLastSync: Long = -1
)