package com.zacle.spendtrack.core.firebase.datasource

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.zacle.spendtrack.core.data.datasource.CategoryDataSource
import com.zacle.spendtrack.core.firebase.util.Collections.CATEGORIES_COLLECTION
import com.zacle.spendtrack.core.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FirebaseCategoryDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
): CategoryDataSource {
    override suspend fun getCategories(): Flow<List<Category>> = flow {
        val task = firestore.collection(CATEGORIES_COLLECTION)
            .get()

        val snapshot = Tasks.await(task, TIMEOUT_DURATION, TimeUnit.SECONDS)
        val categories = snapshot.documents.mapNotNull {
            it.toObject(Category::class.java)
        }
        emit(categories)
    }.catch { e ->
        Timber.e(e)
        emit(emptyList())
    }

    override suspend fun insertAllCategories(categories: List<Category>) {
        val batch = firestore.batch() // Firestore batch write

        categories.forEach { category ->
            val docRef = firestore.collection(CATEGORIES_COLLECTION).document(category.categoryId)
            batch.set(docRef, category)
        }

        try {
            // Commit the batch
            batch.commit().await()
        } catch (e: Exception) {
            // Handle failure
            Timber.e("FirestoreError", "Batch insert failed", e)
        }
    }
}