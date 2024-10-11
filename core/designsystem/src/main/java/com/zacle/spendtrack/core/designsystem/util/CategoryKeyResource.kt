package com.zacle.spendtrack.core.designsystem.util

import android.content.Context
import com.zacle.spendtrack.core.shared_resources.R

object CategoryKeyResource {
    fun getStringResourceForCategory(context: Context, categoryKey: String): String {
        return when (categoryKey) {
            "food_dining" -> context.getString(R.string.food_dining)
            "groceries" -> context.getString(R.string.groceries)
            "shopping" -> context.getString(R.string.shopping)
            "entertainment" -> context.getString(R.string.entertainment)
            "utilities" -> context.getString(R.string.utilities)
            "transportation" -> context.getString(R.string.transportation)
            "rent_housing" -> context.getString(R.string.rent_housing)
            "health_fitness" -> context.getString(R.string.health_fitness)
            "education" -> context.getString(R.string.education)
            "miscellaneous" -> context.getString(R.string.miscellaneous)
            "insurance" -> context.getString(R.string.insurance)
            "travel" -> context.getString(R.string.travel)
            "personal_care" -> context.getString(R.string.personal_care)
            "gifts_donations" -> context.getString(R.string.gifts_donations)
            "savings_investments" -> context.getString(R.string.savings_investments)
            "taxes" -> context.getString(R.string.taxes)
            "pets" -> context.getString(R.string.pets)
            "loans_debt" -> context.getString(R.string.loans_debt)
            "kids" -> context.getString(R.string.kids)
            "business_expenses" -> context.getString(R.string.business_expenses)
            else -> categoryKey
        }
    }

    fun getIconResourceForCategory(categoryKey: String): Int {
        return when (categoryKey) {
            "food_dining" -> R.drawable.food_dinning
            "groceries" -> R.drawable.groceries
            "shopping" -> R.drawable.shopping
            "entertainment" -> R.drawable.entertainment
            "utilities" -> R.drawable.utilities
            "transportation" -> R.drawable.transportation
            "rent_housing" -> R.drawable.rent_housing
            "health_fitness" -> R.drawable.health_fitness
            "education" -> R.drawable.education
            "miscellaneous" -> R.drawable.miscellaneous
            "insurance" -> R.drawable.insurance
            "travel" -> R.drawable.travel
            "personal_care" -> R.drawable.personal_care
            "gifts_donations" -> R.drawable.gift_donation
            "savings_investments" -> R.drawable.savings_investment
            "taxes" -> R.drawable.taxes
            "pets" -> R.drawable.pets
            "loans_debt" -> R.drawable.loans_debt
            "kids" -> R.drawable.kids
            "business_expenses" -> R.drawable.business_expenses
            else -> R.drawable.miscellaneous
        }
    }
}