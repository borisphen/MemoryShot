package com.borisphen.memoryshot.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.borisphen.core.data.mapper.MemoryNoteMapper
import com.borisphen.core.domain.ai.ProcessAiUseCase
import com.borisphen.core.domain.note.DeleteNoteUseCase
import com.borisphen.core.domain.note.GetMemoryNotesUseCase
import com.borisphen.memoryshot.history.presentation.mapper.MemoryNoteUiMapper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class HistoryViewModel @Inject constructor(
    private val getAllNotesUseCase: GetMemoryNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val memoryNoteUiMapper: MemoryNoteUiMapper,
) : ViewModel() {

    class Factory @Inject constructor(
        private val getAllNotesUseCase: GetMemoryNotesUseCase,
        private val deleteNoteUseCase: DeleteNoteUseCase,
        private val memoryNoteUiMapper: MemoryNoteUiMapper,
    ) {
        fun create(): HistoryViewModel = HistoryViewModel(
            getAllNotesUseCase,
            deleteNoteUseCase,
            memoryNoteUiMapper,
        )
    }

    val notes = getAllNotesUseCase()
        .map { notes -> notes.map { memoryNoteUiMapper.toMemoryNoteState(it) } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            deleteNoteUseCase(id)
        }
    }
}