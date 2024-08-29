package com.zacle.spendtrack.core.firebase.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.zacle.spendtrack.core.model.User
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant

data class FirebaseUser(
    @DocumentId val userId: String = "",
    val email: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val firstName: String = "",
    val lastName: String = "",
    val profilePictureUrl: String? = null,
    val updatedAt: Timestamp? = null,
    val synced: Boolean = true
)

fun FirebaseUser.asExternalModel() = User(
    userId = userId,
    email = email,
    createdAt = Instant.fromEpochSeconds(createdAt.seconds, createdAt.nanoseconds),
    firstName = firstName,
    lastName = lastName,
    profilePictureUrl = profilePictureUrl,
    updatedAt = updatedAt?.let { Instant.fromEpochSeconds(it.seconds, it.nanoseconds) },
    synced = synced
)

fun User.asFirebaseModel() = FirebaseUser(
    userId = userId,
    email = email,
    createdAt = Timestamp(createdAt.epochSeconds, createdAt.nanosecondsOfSecond),
    firstName = firstName,
    lastName = lastName,
    profilePictureUrl = profilePictureUrl,
    updatedAt = updatedAt?.toJavaInstant()?.let { Timestamp(it.epochSecond, it.nano) },
    synced = synced
)
