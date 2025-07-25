package com.borisphen.memoryshot.ui

sealed class SideEffect {
    data object StartService : SideEffect()
    data object StopService : SideEffect()
}