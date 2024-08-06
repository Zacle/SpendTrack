package com.zacle.spendtrack.core.firebase.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.firebase.user.FirebaseUser
import com.zacle.spendtrack.core.firebase.user.asExternalModel
import com.zacle.spendtrack.core.firebase.user.asFirebaseModel
import com.zacle.spendtrack.core.model.Exceptions.FirebaseUserNotCreatedException
import com.zacle.spendtrack.core.model.Exceptions.FirebaseUserNotFoundException
import com.zacle.spendtrack.core.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
): UserDataSource {
    override suspend fun getUser(userId: String): Flow<User?> {
        return callbackFlow {
            val subscription = getUserDocument(userId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot == null) {
                        return@addSnapshotListener
                    }
                    val user: User? =
                        if (snapshot.exists())
                            snapshot.toObject(FirebaseUser::class.java)?.asExternalModel()
                        else
                            null
                    trySend(user)
                }

            awaitClose { subscription.remove() }
        }
    }

    override suspend fun updateUser(user: User) {
        val firebaseUser = user.asFirebaseModel()

        getUserDocument(user.userId)
            .set(firebaseUser, SetOptions.merge())
            .await()
    }

    override suspend fun insertUser(user: User) {
        val firebaseUser = user.asFirebaseModel()
        userCollection
            .document(user.userId)
            .set(firebaseUser)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    throw FirebaseUserNotCreatedException()
                }
            }
            .await()
    }

    override suspend fun deleteUser(userId: String) {
        /** make sure the user exists before deleting or throw the user not found exception */
        getUserDocument(userId)
            .get()
            .await()
            .toObject(FirebaseUser::class.java) ?: throw FirebaseUserNotFoundException()

        getUserDocument(userId).delete().await()
    }

    private val userCollection by lazy { firestore.collection(USERS_COLLECTION) }
    private fun getUserDocument(userId: String) = userCollection.document(userId)

    companion object {
        private const val USERS_COLLECTION = "users"
    }
}