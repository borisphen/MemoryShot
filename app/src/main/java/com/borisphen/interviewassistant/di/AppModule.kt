package com.borisphen.interviewassistant.di

import com.borisphen.interviewassistant.config.GroqApiConfig
import com.borisphen.interviewassistant.data.config.ApiConfig
import com.borisphen.interviewassistant.domain.service.ServiceController
import com.borisphen.interviewassistant.service.ServiceControllerImpl
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