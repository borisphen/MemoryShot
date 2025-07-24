package com.borisphen.memoryshot.di

import com.borisphen.core.data.config.ApiConfig
import com.borisphen.core.data.config.GroqApiConfig
import com.borisphen.memoryshot.BuildConfig
import com.borisphen.memoryshot.domain.service.ServiceController
import com.borisphen.memoryshot.service.ServiceControllerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
interface AppModule {

    @Binds
    fun bindServiceController(impl: ServiceControllerImpl): ServiceController

    companion object {
        @Provides
        @Named("groqApiKey")
        fun provideGroqApiKey(): String = BuildConfig.GROQ_API_KEY

        @Provides
        @Singleton
        fun provideApiConfig(
            @Named("groqApiKey") groqApiKey: String
        ): ApiConfig = GroqApiConfig(groqApiKey)
    }
}

