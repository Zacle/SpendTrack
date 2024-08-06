package com.zacle.spendtrack.core.model

import kotlinx.datetime.Instant

data class User(
    val userId: String,
    val email: String,
    val createdAt: Instant,
    val firstName: String = "",
    val lastName: String = "",
    val profilePictureUrl: String? = null,
    val updatedAt: Instant? = null,
    val synced: Boolean = true
)
