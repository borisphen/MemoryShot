package com.borisphen.memoryshot.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borisphen.core.domain.ai.ProcessAiUseCase
import com.borisphen.memoryshot.domain.service.ServiceController
import com.borisphen.memoryshot.presentation.ui.AppState
import com.borisphen.memoryshot.presentation.ui.SideEffect
import com.borisphen.memoryshot.presentation.ui.SideEffect.StartService
import com.borisphen.memoryshot.presentation.ui.SideEffect.StopService
import com.borisphen.memoryshot.presentation.ui.UiEvent
import com.borisphen.memoryshot.presentation.ui.UiEvent.ButtonClick
import com.borisphen.util.mutableSharedFlow
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

class MainViewModel @AssistedInject constructor(
    @Assisted private val useCase: ProcessAiUseCase,
    private val serviceController: ServiceController,
) : ViewModel() {

    @AssistedFactory
    interface Factory {

        fun create(useCase: ProcessAiUseCase): MainViewModel
    }

    private val _answerFlow = MutableSharedFlow<String>()
    val answerFlow: SharedFlow<String> = _answerFlow

    private val _sideEffectFlow = Channel<SideEffect>(Channel.BUFFERED)
    val sideEffectFlow: Flow<SideEffect> = _sideEffectFlow.receiveAsFlow()

    private val viewEventsFlow by mutableSharedFlow<UiEvent>()

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
            useCase(text).fold(
                ifLeft = {

                },
                ifRight = {
                    _answerFlow.emit(it.answer)
                }
            )
        }
    }

    fun onViewEvent(event: UiEvent) {
        viewEventsFlow.tryEmit(event)
    }

    private fun handleViewEvent(event: UiEvent) {
        val currentlyRunning = _uiState.value.serviceIsRunning
        when (event) {
            ButtonClick -> {
                updateState { it.copy(serviceIsRunning = !currentlyRunning) }
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

    private fun updateState(reducer: (AppState) -> AppState) {
        _uiState.update(reducer)
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