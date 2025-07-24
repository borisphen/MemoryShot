package com.borisphen.memoryshot.presentation.di

import com.borisphen.core.domain.ai.ProcessAiUseCase
import com.borisphen.memoryshot.presentation.MainViewModel
import dagger.Component

@Component(
    dependencies = [Dependencies::class]
)
interface AiComponent {

    val viewModelFactory: MainViewModel.Factory

    val useCase: ProcessAiUseCase

    @Component.Factory
    interface Factory {
        fun create(dependencies: Dependencies): AiComponent
    }

    companion object {
        fun factory(): Factory = DaggerAiComponent.factory()
    }
}