package com.zacle.spendtrack.core.firebase.model

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.zacle.spendtrack.core.model.auth.AuthenticatedUserInfo

class FirebaseUserInfo(
    private val firebaseUser: FirebaseUser?
): AuthenticatedUserInfo {

    override fun isSignedIn(): Boolean = firebaseUser != null

    override fun getUserId(): String? = firebaseUser?.uid

    override fun getDisplayName(): String? = firebaseUser?.displayName

    override fun getPhotoUrl(): Uri? = firebaseUser?.photoUrl

    override fun getEmail(): String? = firebaseUser?.email

    override fun getPhoneNumber(): String? = firebaseUser?.phoneNumber

    override fun getLastSignInTimestamp(): Long? = firebaseUser?.metadata?.lastSignInTimestamp

    override fun getCreationTimestamp(): Long? = firebaseUser?.metadata?.creationTimestamp

    override fun isEmailVerified(): Boolean? = firebaseUser?.isEmailVerified

}