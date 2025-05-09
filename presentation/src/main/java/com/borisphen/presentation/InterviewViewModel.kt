package com.borisphen.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.borisphen.interviewassistant.domain.ProcessInterviewUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class InterviewViewModel @AssistedInject constructor(
    @Assisted private val useCase: ProcessInterviewUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {

        fun create(useCase: ProcessInterviewUseCase): InterviewViewModel
    }

    private val _answerFlow = MutableSharedFlow<String>()
    val answerFlow: SharedFlow<String> = _answerFlow

    fun processSpeechInput(text: String) {
        viewModelScope.launch {
            val result = useCase(text)
            _answerFlow.emit(result.answer)
        }
    }
}