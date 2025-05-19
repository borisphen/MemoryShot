package com.borisphen.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borisphen.interviewassistant.domain.ProcessInterviewUseCase
import com.borisphen.interviewassistant.domain.service.ServiceController
import com.borisphen.presentation.ui.AppState
import com.borisphen.presentation.ui.SideEffect
import com.borisphen.presentation.ui.SideEffect.StartService
import com.borisphen.presentation.ui.SideEffect.StopService
import com.borisphen.presentation.ui.UiEvent
import com.borisphen.presentation.ui.UiEvent.ButtonClick
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InterviewViewModel @AssistedInject constructor(
    @Assisted private val useCase: ProcessInterviewUseCase,
    private val serviceController: ServiceController,
) : ViewModel() {

    @AssistedFactory
    interface Factory {

        fun create(useCase: ProcessInterviewUseCase): InterviewViewModel
    }

    private val _answerFlow = MutableSharedFlow<String>()
    val answerFlow: SharedFlow<String> = _answerFlow

    private val _sideEffectFlow = Channel<SideEffect>(Channel.BUFFERED)
    val sideEffectFlow: Flow<SideEffect> = _sideEffectFlow.receiveAsFlow()

    private val viewEventsFlow = MutableSharedFlow<UiEvent>()

    private val _uiState = MutableStateFlow(AppState())
    val uiState = _uiState.asStateFlow()

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewEventsFlow.onEach(::handleViewEvent)
            .launchIn(viewModelScope)
    }

    fun processSpeechInput(text: String) {
        viewModelScope.launch {
            val result = useCase(text)
            _answerFlow.emit(result.answer)
        }
    }

    fun onViewEvent(event: UiEvent) {
        viewModelScope.launch {
            viewEventsFlow.emit(event)
        }
    }

    private fun handleViewEvent(event: UiEvent) {
        val currentlyRunning = _uiState.value.serviceIsRunning
        when (event) {
            ButtonClick -> {
                _uiState.update { it.copy(serviceIsRunning = !currentlyRunning) }
                sendSideEffect(
                    if (currentlyRunning) {
                        StopService
                    } else {
                        StartService
                    }
                )
            }
        }
    }

    private fun sendSideEffect(sideEffect: SideEffect) {
        viewModelScope.launch {
            _sideEffectFlow.send(sideEffect)
        }
    }

    fun startService() {
        serviceController.startInterviewService()
    }

    fun stopService() {
        serviceController.stopInterviewService()
    }
}