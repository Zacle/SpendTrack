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
}