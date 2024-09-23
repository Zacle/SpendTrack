package com.zacle.spendtrack.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal object DatabaseMigrations {
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add the new column to the "expenses" table
            db.execSQL("ALTER TABLE expenses ADD COLUMN local_receipt_image_path TEXT")
            // Add the new column to the "incomes" table
            db.execSQL("ALTER TABLE incomes ADD COLUMN local_receipt_image_path TEXT")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Delete all data from the 'categories' table
            db.execSQL("DELETE FROM categories")
            // Add the new 'key' column
            db.execSQL("ALTER TABLE categories ADD COLUMN `key` TEXT NOT NULL DEFAULT ''")
        }
    }
}