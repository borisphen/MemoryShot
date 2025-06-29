package com.borisphen.memoryshot.presentation.di

import com.borisphen.memoryshot.domain.ProcessInterviewUseCase
import com.borisphen.memoryshot.presentation.InterviewViewModel
import dagger.Component

@Component(
    dependencies = [Dependencies::class]
)
interface InterviewComponent {

    val viewModelFactory: InterviewViewModel.Factory

    val useCase: ProcessInterviewUseCase

    @Component.Factory
    interface Factory {
        fun create(dependencies: Dependencies): InterviewComponent
    }

    companion object {
        fun factory(): Factory = DaggerInterviewComponent.factory()
    }
}