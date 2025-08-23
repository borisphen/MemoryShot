package com.borisphen.core.data.di

import android.content.Context
import com.borisphen.core.data.AiRepositoryImpl
import com.borisphen.core.data.network.service.GroqApiService
import com.borisphen.core.data.ocr.OcrEngineImpl
import com.borisphen.core.data.ocr.TessOcrEngineImpl
import com.borisphen.core.data.screenshot.ScreenCaptureManager
import com.borisphen.core.domain.ai.AiRepository
import com.borisphen.core.domain.ocr.OcrEngine
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
interface DataModule {
    @Binds
    @Singleton
    fun bindRepository(impl: AiRepositoryImpl): AiRepository

    companion object {

        @Provides
        @Singleton
        fun provideRepository(service: GroqApiService): AiRepositoryImpl {
            return AiRepositoryImpl(service)
        }

        @Provides
        @Singleton
        @Named("TextRecognition")
        fun provideOcrEngine(): OcrEngine = OcrEngineImpl()

        @Provides
        @Singleton
        @Named("Tess")
        fun provideTessOcrEngine(context: Context): OcrEngine = TessOcrEngineImpl(context = context)

        @Provides
        @Singleton
        fun provideScreenCaptureManager(context: Context): ScreenCaptureManager =
            ScreenCaptureManager(appContext = context)
    }
}