package com.borisphen.memoryshot.presentation.ui

sealed class SideEffect {
    data object StartService : SideEffect()
    data object StopService : SideEffect()
}