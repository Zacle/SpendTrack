package com.zacle.spendtrack.core.model.auth

import android.net.Uri

/**
 * Mirror the Firebase AuthResult to be used in other modules that don't depend on Firebase
 */
data class AuthResult(
    val uid: String? = null,
    val isNewUser: Boolean? = false,
    val displayName: String? = "",
    val email: String? = "",
    val isEmailVerified: Boolean? = false,
    val photoUrl: Uri? = null,
    val providerId: String? = ""
)
