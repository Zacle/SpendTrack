package com.zacle.spendtrack.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zacle.spendtrack.core.database.R
import com.zacle.spendtrack.core.model.Category
import java.util.UUID

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name")
    val name: Int = 0,
    @ColumnInfo(name = "key", defaultValue = "")
    val key: String = "",
    @ColumnInfo(name = "icon")
    val icon: Int,
    @ColumnInfo(name = "color")
    val color: String
)

fun CategoryEntity.asExternalModel() = Category(
    categoryId = id,
    key = key,
    icon = icon,
    color = color
)

val CATEGORIES = listOf(
    CategoryEntity(key = "food_dining", icon = R.drawable.food_dinning, color =  "#FF7043"),
    CategoryEntity(key = "groceries", icon = R.drawable.groceries, color = "#66BB6A"),
    CategoryEntity(key = "shopping", icon = R.drawable.shopping, color = "#EC407A"),
    CategoryEntity(key = "entertainment", icon = R.drawable.entertainment, color = "#AB47BC"),
    CategoryEntity(key = "utilities", icon = R.drawable.utilities, color = "#FFCA28"),
    CategoryEntity(key = "transportation", icon = R.drawable.transportation, color = "#42A5F5"),
    CategoryEntity(key = "rent_housing", icon = R.drawable.rent_housing, color = "#8D6E63"),
    CategoryEntity(key = "health_fitness", icon = R.drawable.health_fitness, color = "#EF5350"),
    CategoryEntity(key = "education", icon = R.drawable.education, color = "#5C6BC0"),
    CategoryEntity(key = "miscellaneous", icon = R.drawable.miscellaneous, color = "#BDBDBD"),
    CategoryEntity(key = "insurance", icon = R.drawable.insurance, color = "#26A69A"),
    CategoryEntity(key = "travel", icon = R.drawable.travel, color = "#FFA726"),
    CategoryEntity(key = "personal_care", icon = R.drawable.personal_care, color = "#FF8A65"),
    CategoryEntity(key = "gifts_donations", icon = R.drawable.gift_donation, color = "#BA68C8"),
    CategoryEntity(key = "savings_investments", icon = R.drawable.savings_investment, color = "#4DB6AC"),
    CategoryEntity(key = "taxes", icon = R.drawable.taxes, color = "#FFB74D"),
    CategoryEntity(key = "pets", icon = R.drawable.pets, color = "#FFD54F"),
    CategoryEntity(key = "loans_debt", icon = R.drawable.loans_debt, color = "#8E24AA"),
    CategoryEntity(key = "kids", icon = R.drawable.kids, color = "#7986CB"),
    CategoryEntity(key = "business_expenses", icon = R.drawable.business_expenses, color = "#7E57C2")
)
