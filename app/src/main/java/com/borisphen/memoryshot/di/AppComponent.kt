package com.borisphen.memoryshot.di

import android.content.Context
import com.borisphen.core.data.di.DataModule
import com.borisphen.core.data.di.NoteDataModule
import com.borisphen.core.data.di.SpeechModule
import com.borisphen.core.data.network.di.NetworkModule
import com.borisphen.memoryshot.MainViewModel
import com.borisphen.memoryshot.history.presentation.di.HistoryDependencies
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
//        HistoryModule::class,
        AppModule::class]
)
interface AppComponent : HistoryDependencies {

    val viewModelFactory: MainViewModel.Factory

    fun inject(service: ForegroundMemoryShotService)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
