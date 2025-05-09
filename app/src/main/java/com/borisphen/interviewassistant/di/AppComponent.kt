package com.borisphen.interviewassistant.di

import android.content.Context
import com.borisphen.interviewassistant.data.di.DataModule
import com.borisphen.interviewassistant.domain.ProcessInterviewUseCase
import com.borisphen.presentation.InterviewViewModel
import com.borisphen.presentation.di.Dependencies
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class])
interface AppComponent: Dependencies {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}