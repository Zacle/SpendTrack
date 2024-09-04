package com.zacle.spendtrack.core.firebase.datasource

import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.zacle.spendtrack.core.data.datasource.BudgetDataSource
import com.zacle.spendtrack.core.firebase.model.FirebaseBudget
import com.zacle.spendtrack.core.firebase.model.asExternalModel
import com.zacle.spendtrack.core.firebase.model.asFirebaseModel
import com.zacle.spendtrack.core.firebase.util.Collections.BUDGETS_COLLECTION
import com.zacle.spendtrack.core.firebase.util.Collections.USERS_COLLECTION
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Exceptions
import com.zacle.spendtrack.core.model.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val TIMEOUT_DURATION = 30L

/**
 * [BudgetDataSource] implementation for Firebase
 */
class FirebaseBudgetDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
): BudgetDataSource {
    override suspend fun getBudget(
        userId: String,
        budgetId: String
    ): Flow<Budget?>  = flow {
        if (userId.isEmpty() || budgetId.isEmpty()) {
            emit(null)
        }

        val task = budgetCollection(userId)
            .whereEqualTo("budgetId", budgetId)
            .get()

        val snapshot = Tasks.await(task, TIMEOUT_DURATION, TimeUnit.SECONDS)
        val budget = snapshot.documents.firstOrNull()?.toObject(FirebaseBudget::class.java)?.asExternalModel()
        emit(budget)
    }.catch { e ->
        Timber.e(e)
        emit(null)
    }

    override suspend fun getBudgets(userId: String, budgetPeriod: Period): Flow<List<Budget>> = flow {
        if (userId.isEmpty()) {
            emit(emptyList())
        }
        val start = Timestamp(budgetPeriod.start.epochSeconds, budgetPeriod.start.nanosecondsOfSecond)
        val end = Timestamp(budgetPeriod.end.epochSeconds, budgetPeriod.end.nanosecondsOfSecond)
        val task = budgetCollection(userId)
            .whereGreaterThanOrEqualTo("budgetPeriod", start)
            .whereLessThanOrEqualTo("budgetPeriod", end)
            .get()
        val snapshot = Tasks.await(task, TIMEOUT_DURATION, TimeUnit.SECONDS)

        val budgets = snapshot.documents.mapNotNull {
            it.toObject(FirebaseBudget::class.java)?.asExternalModel()
        }
        emit(budgets)
    }.catch { e ->
        Timber.e(e)
        emit(emptyList())
    }

    override suspend fun addBudget(budget: Budget) {
        val firebaseBudget = budget.asFirebaseModel()
        budgetCollection(budget.userId)
            .document(budget.budgetId)
            .set(firebaseBudget)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    throw Exceptions.FirebaseBudgetNotCreatedException()
                }
            }
            .await()
    }

    override suspend fun updateBudget(budget: Budget) {
        val firebaseBudget = budget.asFirebaseModel()
        budgetCollection(budget.userId)
            .document(budget.budgetId)
            .set(firebaseBudget, SetOptions.merge())
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    throw Exceptions.FirebaseBudgetNotUpdatedException()
                }
            }
            .await()
    }

    override suspend fun deleteBudget(userId: String, budgetId: String) {
        /** Make sure the budget exists before deleting or throw the budget not found exception */
        budgetCollection(userId)
            .document(budgetId)
            .get()
            .await()
            .toObject(FirebaseBudget::class.java) ?: throw Exceptions.BudgetNotFoundException()

        budgetCollection(userId).document(budgetId).delete().await()
    }

    private fun budgetCollection(userId: String) =
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(BUDGETS_COLLECTION)

}