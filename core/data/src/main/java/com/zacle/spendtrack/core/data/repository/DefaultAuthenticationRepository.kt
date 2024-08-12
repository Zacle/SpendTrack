package com.zacle.spendtrack.core.data.repository

import android.content.Context
import com.zacle.spendtrack.core.common.di.LocalUserData
import com.zacle.spendtrack.core.common.di.RemoteUserData
import com.zacle.spendtrack.core.data.datasource.AuthenticationDataSource
import com.zacle.spendtrack.core.data.datasource.GoogleAuthDataSource
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.domain.repository.AuthenticationRepository
import com.zacle.spendtrack.core.model.User
import com.zacle.spendtrack.core.model.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import javax.inject.Inject

class DefaultAuthenticationRepository @Inject constructor(
    private val authenticationDataSource: AuthenticationDataSource,
    private val googleAuthDataSource: GoogleAuthDataSource,
    @RemoteUserData private val remoteUserDataSource: UserDataSource,
    @LocalUserData private val localUserDataSource: UserDataSource
): AuthenticationRepository {

    /**
     * Authenticate the user with Google.
     */
    override suspend fun authenticateWithGoogle(context: Context): Flow<AuthResult> = flow {
        googleAuthDataSource.authenticateWithGoogle(context).collect { authResult ->
            if (authResult != null) {
                val inserted = insertNewUser(authResult)
                if (inserted) {
                    emit(authResult)
                } else {
                    emit(AuthResult())
                }
            } else {
                emit(AuthResult())
            }
        }
    }

    /**
     * Sign up the user with email and password. Then send a verification email if the user has been
     * successfully created.
     */
    override suspend fun signUpWithEmailAndPassword(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): AuthResult {
        val authResult = authenticationDataSource.signUpWithEmailAndPassword(email, password)
        saveNewUser(authResult, firstName, lastName, email)
        return authResult
    }

    override suspend fun updatePassword(password: String): Boolean {
        return authenticationDataSource.updatePassword(password)
    }

    override suspend fun sendPasswordResetEmail(email: String): Boolean {
        return authenticationDataSource.sendPasswordResetEmail(email)
    }

    /**
     * Sign in the user with email and password. Then reload the user to get the latest data
     */
    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        val authResult = authenticationDataSource.signInWithEmailAndPassword(email, password)
        authenticationDataSource.reloadUser()
        return authResult
    }

    override suspend fun signOut() {
        authenticationDataSource.signOut()
    }

    /**
     * Save the user authenticated with email and password into the database.
     *
     * We insert the user into the database only if it is a new user:
     * user is inserted first in the remote database, then in the local database
     *
     * @return true if the user was inserted successfully, false otherwise
     */
    private suspend fun saveNewUser(
        authResult: AuthResult,
        firstName: String,
        lastName: String,
        email: String
    ): Boolean {
        return if (authResult.isNewUser == true && authResult.uid != null) {
            val user = User(
                userId = authResult.uid!!,
                email = email,
                createdAt = Clock.System.now(),
                firstName = firstName,
                lastName = lastName,
                profilePictureUrl = authResult.photoUrl?.toString()
            )
            remoteUserDataSource.insertUser(user)
            localUserDataSource.insertUser(user)
            true
        } else {
            false
        }
    }

    /**
     * Insert the user authenticated with Google into the database.
     *
     * We insert the user into the database only if it is a new user:
     * user is inserted first in the remote database, then in the local database
     *
     * @return true if the user was inserted successfully, false otherwise
     */
    private suspend fun insertNewUser(authResult: AuthResult): Boolean =
        try {
            if (authResult.isNewUser == true && authResult.uid != null) {
                val user = User(
                    userId = authResult.uid!!,
                    email = authResult.email ?: "",
                    createdAt = Clock.System.now(),
                    firstName = authResult.displayName ?: "",
                    profilePictureUrl = authResult.photoUrl?.toString()
                )
                remoteUserDataSource.insertUser(user)
                localUserDataSource.insertUser(user)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }

}