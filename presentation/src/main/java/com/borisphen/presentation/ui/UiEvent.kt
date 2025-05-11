package com.borisphen.presentation.ui

sealed class UiEvent {
    data object ButtonClick : UiEvent()
}