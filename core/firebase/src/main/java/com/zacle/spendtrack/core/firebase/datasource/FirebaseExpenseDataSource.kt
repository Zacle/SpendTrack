package com.zacle.spendtrack.core.firebase.datasource

import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.zacle.spendtrack.core.data.datasource.ExpenseDataSource
import com.zacle.spendtrack.core.firebase.model.FirebaseExpense
import com.zacle.spendtrack.core.firebase.model.asExternalModel
import com.zacle.spendtrack.core.firebase.model.asFirebaseModel
import com.zacle.spendtrack.core.firebase.util.Collections.EXPENSES_COLLECTION
import com.zacle.spendtrack.core.firebase.util.Collections.USERS_COLLECTION
import com.zacle.spendtrack.core.model.Exceptions
import com.zacle.spendtrack.core.model.Expense
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * [ExpenseDataSource] implementation for Firebase
 */
class FirebaseExpenseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
): ExpenseDataSource {
    override suspend fun getExpense(userId: String, expenseId: String): Flow<Expense?> = flow {
        if (userId.isEmpty() || expenseId.isEmpty()) {
            emit(null)
        }

        val task = expenseCollection(userId)
            .whereEqualTo("expenseId", expenseId)
            .get()

        val snapshot = Tasks.await(task, TIMEOUT_DURATION, TimeUnit.SECONDS)
        val expense = snapshot.documents.firstOrNull()?.toObject(FirebaseExpense::class.java)?.asExternalModel()
        emit(expense)
    }.catch { e ->
        Timber.e(e)
        emit(null)
    }

    override suspend fun getExpenses(userId: String, period: Period): Flow<List<Expense>> {
        if (userId.isEmpty()) {
            return flowOf(emptyList())
        }

        try {
            val start = Timestamp(period.start.epochSeconds, period.start.nanosecondsOfSecond)
            val end = Timestamp(period.end.epochSeconds, period.end.nanosecondsOfSecond)
            val task = expenseCollection(userId)
                .whereGreaterThanOrEqualTo("transactionDate", start)
                .whereLessThanOrEqualTo("transactionDate", end)
                .get()

            val snapshot = Tasks.await(task, TIMEOUT_DURATION, TimeUnit.SECONDS)
            val expenses = snapshot.documents.mapNotNull {
                it.toObject(FirebaseExpense::class.java)?.asExternalModel()
            }
            return flowOf(expenses)
        } catch (e: Exception) {
            Timber.e(e)
            return flowOf(emptyList())
        }
    }

    override suspend fun getExpensesByCategory(
        userId: String,
        categoryId: String,
        period: Period
    ): Flow<List<Expense>> {
        if (userId.isEmpty()) {
            return flowOf(emptyList())
        }

        try {
            val start = Timestamp(period.start.epochSeconds, period.start.nanosecondsOfSecond)
            val end = Timestamp(period.end.epochSeconds, period.end.nanosecondsOfSecond)
            val task = expenseCollection(userId)
                .whereEqualTo("category.categoryId", categoryId)
                .whereGreaterThanOrEqualTo("transactionDate", start)
                .whereLessThanOrEqualTo("transactionDate", end)
                .get()

            val snapshot = Tasks.await(task, TIMEOUT_DURATION, TimeUnit.SECONDS)
            val expenses = snapshot.documents.mapNotNull {
                it.toObject(FirebaseExpense::class.java)?.asExternalModel()
            }
            return flowOf(expenses)
        } catch (e: Exception) {
            Timber.e(e)
            return flowOf(emptyList())
        }
    }

    override suspend fun addAllExpenses(expenses: List<Expense>) {
        val batch = firestore.batch() // Firestore batch write

        expenses.forEach { expense ->
            val docRef = expenseCollection(expense.userId).document(expense.id)
            batch.set(docRef, expense.asFirebaseModel())
        }

        try {
            // Commit the batch
            batch.commit().await()
        } catch (e: Exception) {
            // Handle failure
            Timber.e("FirestoreError", "Batch insert failed", e)
        }
    }

    override suspend fun addExpense(expense: Expense) {
        val firebaseExpense = expense.asFirebaseModel()
        expenseCollection(expense.userId)
            .document(expense.id)
            .set(firebaseExpense)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    throw Exceptions.FirebaseExpenseNotCreatedException()
                }
            }
            .await()
    }

    override suspend fun updateExpense(expense: Expense) {
        val firebaseExpense = expense.asFirebaseModel()
        expenseCollection(expense.userId)
            .document(expense.id)
            .set(firebaseExpense, SetOptions.merge())
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    throw Exceptions.FirebaseExpenseNotUpdatedException()
                }
            }
            .await()
    }

    override suspend fun deleteExpense(userId: String, expenseId: String) {
        /** Make sure the expense exists before deleting or throw the expense not found exception **/
        expenseCollection(userId)
            .document(expenseId)
            .get()
            .await()
            .toObject(FirebaseExpense::class.java) ?: throw Exceptions.ExpenseNotFoundException()

        expenseCollection(userId).document(expenseId).delete().await()
    }

    private fun expenseCollection(userId: String) =
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(EXPENSES_COLLECTION)
}