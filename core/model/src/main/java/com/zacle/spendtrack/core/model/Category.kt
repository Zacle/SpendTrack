package com.zacle.spendtrack.core.model

import java.util.UUID

data class Category(
    val categoryId: String = UUID.randomUUID().toString(),
    val key: String = "",
    val icon: Int = 0,
    val color: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Category) return false

        return categoryId == other.categoryId
    }

    override fun hashCode(): Int {
        return categoryId.hashCode()
    }
}