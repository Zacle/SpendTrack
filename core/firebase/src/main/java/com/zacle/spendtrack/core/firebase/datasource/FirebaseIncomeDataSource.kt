package com.zacle.spendtrack.core.firebase.datasource

import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.zacle.spendtrack.core.data.datasource.IncomeDataSource
import com.zacle.spendtrack.core.firebase.model.FirebaseIncome
import com.zacle.spendtrack.core.firebase.model.asExternalModel
import com.zacle.spendtrack.core.firebase.model.asFirebaseModel
import com.zacle.spendtrack.core.firebase.util.Collections.INCOMES_COLLECTION
import com.zacle.spendtrack.core.firebase.util.Collections.USERS_COLLECTION
import com.zacle.spendtrack.core.model.Exceptions
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * [IncomeDataSource] implementation for Firebase
 */
class FirebaseIncomeDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
): IncomeDataSource {
    override suspend fun getIncome(userId: String, incomeId: String): Flow<Income?> = flow {
        if (userId.isEmpty() || incomeId.isEmpty()) {
            emit(null)
        }

        val task = incomeCollection(userId)
            .whereEqualTo("incomeId", incomeId)
            .get()

        val snapshot = Tasks.await(task, TIMEOUT_DURATION, TimeUnit.SECONDS)
        val income = snapshot.documents.firstOrNull()?.toObject(FirebaseIncome::class.java)?.asExternalModel()
        emit(income)
    }.catch { e ->
        Timber.e(e)
        emit(null)
    }

    override suspend fun getIncomes(userId: String, period: Period): Flow<List<Income>> = flow {
        if (userId.isEmpty()) {
            emit(emptyList())
        }

        val start = Timestamp(period.start.epochSeconds, period.start.nanosecondsOfSecond)
        val end = Timestamp(period.end.epochSeconds, period.end.nanosecondsOfSecond)
        val task = incomeCollection(userId)
            .whereGreaterThanOrEqualTo("transactionDate", start)
            .whereLessThanOrEqualTo("transactionDate", end)
            .get()

        val snapshot = Tasks.await(task, TIMEOUT_DURATION, TimeUnit.SECONDS)
        val incomes = snapshot.documents.mapNotNull {
            it.toObject(FirebaseIncome::class.java)?.asExternalModel()
        }
        emit(incomes)
    }.catch { e ->
        Timber.e(e)
        emit(emptyList())
    }

    override suspend fun getIncomesByCategory(
        userId: String,
        categoryId: String,
        period: Period
    ): Flow<List<Income>> = flow {
        if (userId.isEmpty()) {
            emit(emptyList())
        }

        val start = Timestamp(period.start.epochSeconds, period.start.nanosecondsOfSecond)
        val end = Timestamp(period.end.epochSeconds, period.end.nanosecondsOfSecond)
        val task = incomeCollection(userId)
            .whereEqualTo("category.categoryId", categoryId)
            .whereGreaterThanOrEqualTo("transactionDate", start)
            .whereLessThanOrEqualTo("transactionDate", end)
            .get()

        val snapshot = Tasks.await(task, TIMEOUT_DURATION, TimeUnit.SECONDS)
        val incomes = snapshot.documents.mapNotNull {
            it.toObject(FirebaseIncome::class.java)?.asExternalModel()
        }
        emit(incomes)
    }.catch { e ->
        Timber.e(e)
        emit(emptyList())
    }

    override suspend fun addIncome(income: Income) {
        val firebaseIncome = income.asFirebaseModel()
        incomeCollection(income.userId)
            .document(income.incomeId)
            .set(firebaseIncome)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    throw Exceptions.FirebaseIncomeNotCreatedException()
                }
            }
            .await()
    }

    override suspend fun updateIncome(income: Income) {
        val firebaseIncome = income.asFirebaseModel()
        incomeCollection(income.userId)
            .document(income.incomeId)
            .set(firebaseIncome, SetOptions.merge())
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    throw Exceptions.FirebaseIncomeNotUpdatedException()
                }
            }
            .await()
    }

    override suspend fun deleteIncome(userId: String, incomeId: String) {
        /** Make sure the income exists before deleting or throw the income not found exception **/
        incomeCollection(userId)
            .document(incomeId)
            .get()
            .await()
            .toObject(FirebaseIncome::class.java) ?: throw Exceptions.IncomeNotFoundException()

        incomeCollection(userId).document(incomeId).delete().await()
    }

    private fun incomeCollection(userId: String) =
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(INCOMES_COLLECTION)

}