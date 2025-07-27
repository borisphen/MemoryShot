package com.borisphen.memoryshot.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borisphen.core.domain.ai.ProcessAiUseCase
import com.borisphen.core.domain.note.DeleteNoteUseCase
import com.borisphen.core.domain.note.GetMemoryNotesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val getAllNotesUseCase: GetMemoryNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    interface Factory {

        fun create(useCase: ProcessAiUseCase): HistoryViewModel
    }

    val notes = getAllNotesUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            deleteNoteUseCase(id)
        }
    }
}