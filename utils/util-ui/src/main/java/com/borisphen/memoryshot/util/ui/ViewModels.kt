package com.borisphen.memoryshot.util.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
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

inline fun <reified T : ViewModel> ComponentActivity.activityViewModel(
    noinline ownerProducer: () -> ViewModelStore = { this.viewModelStore },
    crossinline creator: () -> T,
): Lazy<T> = ViewModelLazy(
    viewModelClass = T::class,
    storeProducer = ownerProducer,
    factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return creator.invoke() as T
            }
        }
    }
)

