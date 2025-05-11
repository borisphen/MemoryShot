package com.borisphen.presentation.di

import com.borisphen.interviewassistant.domain.ProcessInterviewUseCase
import com.borisphen.presentation.InterviewViewModel
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