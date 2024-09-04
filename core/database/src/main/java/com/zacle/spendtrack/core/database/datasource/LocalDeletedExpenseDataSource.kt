package com.zacle.spendtrack.core.database.datasource

import com.zacle.spendtrack.core.data.datasource.DeletedExpenseDataSource
import com.zacle.spendtrack.core.database.dao.DeletedExpenseDao
import com.zacle.spendtrack.core.database.model.asEntity
import com.zacle.spendtrack.core.database.model.asExternalModel
import com.zacle.spendtrack.core.model.DeletedExpense
import javax.inject.Inject

class LocalDeletedExpenseDataSource @Inject constructor(
    private val deletedExpenseDao: DeletedExpenseDao
): DeletedExpenseDataSource {
    override suspend fun insert(deletedExpense: DeletedExpense) {
        deletedExpenseDao.insert(deletedExpense.asEntity())
    }

    override suspend fun delete(userId: String, expenseId: String) {
        deletedExpenseDao.delete(userId, expenseId)
    }

    override suspend fun getDeletedExpenses(userId: String): List<DeletedExpense> =
        deletedExpenseDao.getDeletedExpenses(userId).map { it.asExternalModel() }
}