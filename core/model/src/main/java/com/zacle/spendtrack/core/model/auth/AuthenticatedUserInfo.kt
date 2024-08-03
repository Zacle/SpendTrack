package com.zacle.spendtrack.core.model.auth

import android.net.Uri

/**
 * Interface to decouple the user info from Firebase
 */
interface AuthenticatedUserInfo {
    fun isSignedIn(): Boolean
    fun getUserId(): String?
    fun getDisplayName(): String?
    fun getPhotoUrl(): Uri?
    fun getEmail(): String?
    fun getPhoneNumber(): String?
    fun getLastSignInTimestamp(): Long?
    fun getCreationTimestamp(): Long?
    fun isEmailVerified(): Boolean?
}