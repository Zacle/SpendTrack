package com.zacle.spendtrack.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zacle.spendtrack.core.model.User
import kotlinx.datetime.Instant

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "first_name") val firstName: String = "",
    @ColumnInfo(name = "last_name") val lastName: String = "",
    @ColumnInfo(name = "profile_picture_url") val profilePictureUrl: String? = null,
    @ColumnInfo(name = "local_receipt_image_path") val localReceiptImagePath: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant? = null,
    val synced: Boolean = false
)

fun UserEntity.asExternalModel() = User(
    userId = id,
    email = email,
    createdAt = createdAt,
    firstName = firstName,
    lastName = lastName,
    profilePictureUrl = profilePictureUrl,
    localProfilePictureUrl = localReceiptImagePath,
    updatedAt = updatedAt,
    synced = synced
)

fun User.asEntity() = UserEntity(
    id = userId,
    email = email,
    createdAt = createdAt,
    firstName = firstName,
    lastName = lastName,
    profilePictureUrl = profilePictureUrl,
    localReceiptImagePath = localProfilePictureUrl,
    updatedAt = updatedAt,
    synced = synced
)
