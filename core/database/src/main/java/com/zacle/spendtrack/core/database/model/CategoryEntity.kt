package com.zacle.spendtrack.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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

fun Category.asEntity() = CategoryEntity(
    id = categoryId,
    key = key,
    icon = icon,
    color = color
)
