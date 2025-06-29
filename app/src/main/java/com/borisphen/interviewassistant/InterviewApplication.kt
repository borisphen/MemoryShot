package com.borisphen.memoryshot

import android.app.Application
import com.borisphen.memoryshot.di.AppComponent
import com.borisphen.memoryshot.di.DaggerAppComponent

class InterviewApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.factory().create(this)
    }

    companion object {
        lateinit var appComponent: AppComponent
            private set
    }
}

