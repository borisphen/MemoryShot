package com.borisphen.memoryshot.ui

sealed class UiEvent {
    data object ButtonClick : UiEvent()
}