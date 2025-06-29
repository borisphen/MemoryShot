package com.borisphen.memoryshot.presentation.ui

sealed class UiEvent {
    data object ButtonClick : UiEvent()
}