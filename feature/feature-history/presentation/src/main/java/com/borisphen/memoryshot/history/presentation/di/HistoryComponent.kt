package com.borisphen.memoryshot.history.presentation.di

import com.borisphen.core.domain.ai.ProcessAiUseCase
import com.borisphen.memoryshot.history.presentation.HistoryViewModel
import dagger.Component

@Component(
    dependencies = [HistoryDependencies::class],
    modules = [HistoryModule::class]
)
interface HistoryComponent {

    val viewModelFactory: HistoryViewModel.Factory

    @Component.Factory
    interface Factory {
        fun create(historyDependencies: HistoryDependencies): HistoryComponent
    }

    companion object {
        fun factory(): Factory = DaggerHistoryComponent.factory()
    }
}