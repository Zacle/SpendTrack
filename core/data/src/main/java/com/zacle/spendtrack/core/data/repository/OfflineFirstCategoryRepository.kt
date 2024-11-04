package com.zacle.spendtrack.core.data.repository

import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.common.di.LocalCategoryData
import com.zacle.spendtrack.core.common.di.RemoteCategoryData
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.datasource.CategoryDataSource
import com.zacle.spendtrack.core.domain.repository.CategoryRepository
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.shared_resources.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class OfflineFirstCategoryRepository @Inject constructor(
    @LocalCategoryData private val localCategoryDataSource: CategoryDataSource,
    @RemoteCategoryData private val remoteCategoryDataSource: CategoryDataSource,
    @STDispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkMonitor: NetworkMonitor
): CategoryRepository {
    override suspend fun categories(): Flow<List<Category>> =
        localCategoryDataSource.getCategories().flatMapLatest { localCategories ->
            networkMonitor.isOnline.flatMapLatest { isOnline ->
                when {
                    isOnline -> {
                        // If online, try fetching remote categories
                        remoteCategoryDataSource.getCategories().flatMapLatest { remoteCategories ->
                            if (remoteCategories.isNotEmpty()) {
                                localCategoryDataSource.insertAllCategories(remoteCategories)
                                flowOf(remoteCategories)
                            } else {
                                localCategoryDataSource.insertAllCategories(CATEGORIES)
                                remoteCategoryDataSource.insertAllCategories(CATEGORIES)
                                flowOf(CATEGORIES)
                            }
                        }
                    }
                    localCategories.isNotEmpty() -> {
                        // If local categories exist, just emit them
                        flowOf(localCategories)
                    }
                    else -> {
                        // If offline and no local categories, insert and emit defaults
                        localCategoryDataSource.insertAllCategories(CATEGORIES)
                        flowOf(CATEGORIES)
                    }
                }
            }
        }.flowOn(ioDispatcher)
}

val CATEGORIES = listOf(
    Category(key = "food_dining", icon = R.drawable.food_dinning, color =  "#FF7043"),
    Category(key = "groceries", icon = R.drawable.groceries, color = "#66BB6A"),
    Category(key = "shopping", icon = R.drawable.shopping, color = "#EC407A"),
    Category(key = "entertainment", icon = R.drawable.entertainment, color = "#AB47BC"),
    Category(key = "utilities", icon = R.drawable.utilities, color = "#FFCA28"),
    Category(key = "transportation", icon = R.drawable.transportation, color = "#42A5F5"),
    Category(key = "rent_housing", icon = R.drawable.rent_housing, color = "#8D6E63"),
    Category(key = "health_fitness", icon = R.drawable.health_fitness, color = "#EF5350"),
    Category(key = "education", icon = R.drawable.education, color = "#5C6BC0"),
    Category(key = "miscellaneous", icon = R.drawable.miscellaneous, color = "#BDBDBD"),
    Category(key = "insurance", icon = R.drawable.insurance, color = "#26A69A"),
    Category(key = "travel", icon = R.drawable.travel, color = "#FFA726"),
    Category(key = "personal_care", icon = R.drawable.personal_care, color = "#FF8A65"),
    Category(key = "gifts_donations", icon = R.drawable.gift_donation, color = "#BA68C8"),
    Category(key = "savings_investments", icon = R.drawable.savings_investment, color = "#4DB6AC"),
    Category(key = "taxes", icon = R.drawable.taxes, color = "#FFB74D"),
    Category(key = "pets", icon = R.drawable.pets, color = "#FFD54F"),
    Category(key = "loans_debt", icon = R.drawable.loans_debt, color = "#8E24AA"),
    Category(key = "kids", icon = R.drawable.kids, color = "#7986CB"),
    Category(key = "business_expenses", icon = R.drawable.business_expenses, color = "#7E57C2")
)