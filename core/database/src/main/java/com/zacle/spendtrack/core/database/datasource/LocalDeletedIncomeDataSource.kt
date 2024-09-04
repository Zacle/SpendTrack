package com.zacle.spendtrack.core.database.datasource

import com.zacle.spendtrack.core.data.datasource.DeletedIncomeDataSource
import com.zacle.spendtrack.core.database.dao.DeletedIncomeDao
import com.zacle.spendtrack.core.database.model.asEntity
import com.zacle.spendtrack.core.database.model.asExternalModel
import com.zacle.spendtrack.core.model.DeletedIncome
import javax.inject.Inject

class LocalDeletedIncomeDataSource @Inject constructor(
    private val deletedIncomeDao: DeletedIncomeDao
): DeletedIncomeDataSource {
    override suspend fun insert(deletedIncome: DeletedIncome) {
        deletedIncomeDao.insert(deletedIncome.asEntity())
    }

    override suspend fun delete(userId: String, incomeId: String) {
        deletedIncomeDao.delete(userId, incomeId)
    }

    override suspend fun getDeletedIncomes(userId: String): List<DeletedIncome> =
        deletedIncomeDao.getDeletedIncomes(userId).map { it.asExternalModel() }
}