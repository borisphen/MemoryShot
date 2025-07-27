package com.borisphen.memoryshot.voice.presentation.di

import com.borisphen.core.domain.ai.ProcessAiUseCase
import dagger.Component

@Component(
    dependencies = [Dependencies::class]
)
interface AiComponent {

    val useCase: ProcessAiUseCase

    @Component.Factory
    interface Factory {
        fun create(dependencies: Dependencies): AiComponent
    }

    companion object {
        fun factory(): Factory = DaggerAiComponent.factory()
    }
}