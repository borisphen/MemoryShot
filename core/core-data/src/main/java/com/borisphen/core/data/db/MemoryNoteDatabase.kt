package com.borisphen.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [MemoryNoteEntity::class], version = 2)
abstract class MemoryNoteDatabase : RoomDatabase() {
    abstract fun memoryNoteDao(): MemoryNoteDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE memory_notes ADD COLUMN ocrText TEXT")
                db.execSQL("ALTER TABLE memory_notes ADD COLUMN screenshotPath TEXT")
            }
        }
    }
}
