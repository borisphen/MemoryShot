package com.borisphen.memoryshot.di

import com.borisphen.memoryshot.config.GroqApiConfig
import com.borisphen.memoryshot.data.config.ApiConfig
import com.borisphen.memoryshot.domain.service.ServiceController
import com.borisphen.memoryshot.service.ServiceControllerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface AppModule {

    @Binds
    fun bindServiceController(impl: ServiceControllerImpl): ServiceController

    companion object {
        @Provides
        fun provideAppConfig(): ApiConfig = GroqApiConfig()
    }
}

