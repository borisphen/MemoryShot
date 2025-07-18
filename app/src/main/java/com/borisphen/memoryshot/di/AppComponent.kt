package com.borisphen.memoryshot.di

import android.content.Context
import com.borisphen.memoryshot.data.di.DataModule
import com.borisphen.memoryshot.data.di.NetworkModule
import com.borisphen.memoryshot.presentation.di.Dependencies
import com.borisphen.memoryshot.presentation.di.SpeechModule
import com.borisphen.memoryshot.service.ForegroundMemoryShotService
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

    fun inject(service: ForegroundMemoryShotService)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
