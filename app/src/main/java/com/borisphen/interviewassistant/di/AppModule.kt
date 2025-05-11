package com.borisphen.interviewassistant.di

import com.borisphen.interviewassistant.domain.service.ServiceController
import com.borisphen.interviewassistant.service.ServiceControllerImpl
import dagger.Binds
import dagger.Module

@Module
interface AppModule {

    @Binds
    fun bindServiceController(impl: ServiceControllerImpl): ServiceController
}