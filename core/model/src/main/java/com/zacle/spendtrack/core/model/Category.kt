package com.zacle.spendtrack.core.model

import java.util.UUID

data class Category(
    val categoryId: String = UUID.randomUUID().toString(),
    val key: String = "",
    val icon: Int = 0,
    val color: String = ""
)