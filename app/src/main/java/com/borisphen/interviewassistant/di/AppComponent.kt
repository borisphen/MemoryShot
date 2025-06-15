package com.borisphen.interviewassistant.di

import android.content.Context
import com.borisphen.interviewassistant.data.di.DataModule
import com.borisphen.interviewassistant.data.di.NetworkModule
import com.borisphen.interviewassistant.service.ForegroundInterviewService
import com.borisphen.presentation.di.Dependencies
import com.borisphen.presentation.di.SpeechModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [DataModule::class,
        SpeechModule::class,
        NetworkModule::class,
        AppModule::class]
)
interface AppComponent : Dependencies {

    fun inject(service: ForegroundInterviewService)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}