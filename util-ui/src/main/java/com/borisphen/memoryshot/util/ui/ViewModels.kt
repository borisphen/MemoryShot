package com.borisphen.memoryshot.util.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

@Composable
inline fun <reified T : ViewModel> composeViewModel(
    key: String? = null,
    crossinline creator: () -> T,
): T {
    return viewModel(
        modelClass = T::class,
        key = key?.plus(T::class.qualifiedName),
        factory = viewModelFactory {
            initializer {
                creator.invoke()
            }
        },
    )
}
