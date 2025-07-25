package com.borisphen.memoryshot.di

import android.content.Context
import com.borisphen.core.data.di.DataModule
import com.borisphen.core.data.di.NoteDataModule
import com.borisphen.core.data.di.SpeechModule
import com.borisphen.core.data.network.di.NetworkModule
import com.borisphen.memoryshot.MainViewModel
import com.borisphen.memoryshot.presentation.di.Dependencies
import com.borisphen.memoryshot.service.ForegroundMemoryShotService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [DataModule::class,
        SpeechModule::class,
        NetworkModule::class,
        NoteDataModule::class,
        AppModule::class]
)
interface AppComponent : Dependencies {

    val viewModelFactory: MainViewModel.Factory

    fun inject(service: ForegroundMemoryShotService)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
