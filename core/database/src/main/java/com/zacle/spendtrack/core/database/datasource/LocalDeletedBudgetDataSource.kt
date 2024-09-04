package com.zacle.spendtrack.core.database.datasource

import com.zacle.spendtrack.core.data.datasource.DeletedBudgetDataSource
import com.zacle.spendtrack.core.database.dao.DeletedBudgetDao
import com.zacle.spendtrack.core.database.model.asEntity
import com.zacle.spendtrack.core.database.model.asExternalModel
import com.zacle.spendtrack.core.model.DeletedBudget
import javax.inject.Inject

class LocalDeletedBudgetDataSource @Inject constructor(
    private val deletedBudgetDao: DeletedBudgetDao
): DeletedBudgetDataSource {
    override suspend fun insert(deletedBudget: DeletedBudget) {
        deletedBudgetDao.insert(deletedBudget.asEntity())
    }

    override suspend fun delete(userId: String, budgetId: String) {
        deletedBudgetDao.delete(userId, budgetId)
    }

    override suspend fun getDeletedBudgets(userId: String): List<DeletedBudget> {
        return deletedBudgetDao.getDeletedBudgets(userId).map { it.asExternalModel() }
    }
}