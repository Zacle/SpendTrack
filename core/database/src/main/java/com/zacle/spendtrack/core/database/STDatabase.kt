package com.zacle.spendtrack.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zacle.spendtrack.core.database.converters.InstantConverter
import com.zacle.spendtrack.core.database.dao.UserDao
import com.zacle.spendtrack.core.database.model.UserEntity

@Database(
    entities = [
        UserEntity::class
    ],
    version = 1
)
@TypeConverters(
    InstantConverter::class
)
abstract class STDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}