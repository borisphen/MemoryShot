package com.borisphen.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MemoryNoteEntity::class], version = 1)
abstract class MemoryNoteDatabase : RoomDatabase() {
    abstract fun memoryNoteDao(): MemoryNoteDao
}