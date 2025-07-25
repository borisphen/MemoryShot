package com.borisphen.core.data.di

import android.content.Context
import androidx.room.Room
import com.borisphen.core.data.db.MemoryNoteDao
import com.borisphen.core.data.db.MemoryNoteDatabase
import com.borisphen.core.data.mapper.MemoryNoteMapper
import com.borisphen.core.data.repository.MemoryNoteRepositoryImpl
import com.borisphen.core.domain.note.MemoryNoteRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object NoteDataModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): MemoryNoteDatabase {
        return Room.databaseBuilder(
            context,
            MemoryNoteDatabase::class.java,
            "memory_note.db"
        ).build()
    }

    @Provides
    fun provideDao(db: MemoryNoteDatabase): MemoryNoteDao = db.memoryNoteDao()

    @Provides
    fun provideRepository(
        dao: MemoryNoteDao,
        mapper: MemoryNoteMapper
    ): MemoryNoteRepository =
        MemoryNoteRepositoryImpl(dao = dao, mapper = mapper)

    @Provides
    fun provideNoteMapper(moshi: Moshi): MemoryNoteMapper =
        MemoryNoteMapper(moshi)
}