package com.zacle.spendtrack.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zacle.spendtrack.core.database.R
import com.zacle.spendtrack.core.model.Category
import java.util.UUID

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: Int,
    val icon: Int,
    val color: String
)

fun CategoryEntity.asExternalModel() = Category(
    categoryId = id,
    name = name,
    icon = icon,
    color = color
)

val CATEGORIES = listOf(
    CategoryEntity(name = R.string.food_dining, icon = R.drawable.food_dinning, color =  "#FF7043"),
    CategoryEntity(name = R.string.groceries, icon = R.drawable.groceries, color = "#66BB6A"),
    CategoryEntity(name = R.string.shopping, icon = R.drawable.shopping, color = "#EC407A"),
    CategoryEntity(name = R.string.entertainment, icon = R.drawable.entertainment, color = "#AB47BC"),
    CategoryEntity(name = R.string.utilities, icon = R.drawable.utilities, color = "#FFCA28"),
    CategoryEntity(name = R.string.transportation, icon = R.drawable.transportation, color = "#42A5F5"),
    CategoryEntity(name = R.string.rent_housing, icon = R.drawable.rent_housing, color = "#8D6E63"),
    CategoryEntity(name = R.string.health_fitness, icon = R.drawable.health_fitness, color = "#EF5350"),
    CategoryEntity(name = R.string.education, icon = R.drawable.education, color = "#5C6BC0"),
    CategoryEntity(name = R.string.miscellaneous, icon = R.drawable.miscellaneous, color = "#BDBDBD"),
    CategoryEntity(name = R.string.insurance, icon = R.drawable.insurance, color = "#26A69A"),
    CategoryEntity(name = R.string.travel, icon = R.drawable.travel, color = "#FFA726"),
    CategoryEntity(name = R.string.personal_care, icon = R.drawable.personal_care, color = "#FF8A65"),
    CategoryEntity(name = R.string.gifts_donations, icon = R.drawable.gift_donation, color = "#BA68C8"),
    CategoryEntity(name = R.string.savings_investments, icon = R.drawable.savings_investment, color = "#4DB6AC"),
    CategoryEntity(name = R.string.taxes, icon = R.drawable.taxes, color = "#FFB74D"),
    CategoryEntity(name = R.string.pets, icon = R.drawable.pets, color = "#FFD54F"),
    CategoryEntity(name = R.string.loans_debt, icon = R.drawable.loans_debt, color = "#8E24AA"),
    CategoryEntity(name = R.string.kids, icon = R.drawable.kids, color = "#7986CB"),
    CategoryEntity(name = R.string.business_expenses, icon = R.drawable.business_expenses, color = "#7E57C2")
)
