package com.borisphen.memoryshot.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.borisphen.core.data.config.GroqApiConfig
import com.borisphen.core.data.screenshot.ScreenshotRepositoryImpl
import com.borisphen.core.data.sharedpreferences.PreferenceStorageImpl
import com.borisphen.core.domain.ai.AiRepository
import com.borisphen.core.domain.ai.CreateNoteWithContextUseCase
import com.borisphen.core.domain.config.ApiConfig
import com.borisphen.core.domain.note.MemoryNoteRepository
import com.borisphen.core.domain.ocr.OcrEngine
import com.borisphen.core.domain.screenshot.ProcessScreenshotUseCase
import com.borisphen.core.domain.screenshot.SaveScreenshotUseCase
import com.borisphen.core.domain.screenshot.ScreenshotRepository
import com.borisphen.core.domain.service.ServiceController
import com.borisphen.core.domain.sharedpreferences.PreferenceStorage
import com.borisphen.memoryshot.BuildConfig
import com.borisphen.memoryshot.service.ServiceControllerImpl
import com.borisphen.memoryshot.util.platform.ScreenshotSaver
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

        @Provides
        @Singleton
        fun provideCreateNoteWithContextUseCase(
            aiRepository: AiRepository,
            memoryNoteRepository: MemoryNoteRepository
        ) = CreateNoteWithContextUseCase(
            aiRepository = aiRepository,
            memoryNoteRepository = memoryNoteRepository,
        )

        @Provides
        @Singleton
        fun providePreferenceStorage(
            sharedPreferences: SharedPreferences
        ): PreferenceStorage = PreferenceStorageImpl(
            sharedPreferences = sharedPreferences
        )

        @Provides
        @Singleton
        fun providePrefs(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        @Provides
        @Singleton
        fun provideScreenshotSaver(context: Context): ScreenshotSaver {
            return ScreenshotSaver(context)
        }

        @Provides
        @Singleton
        fun provideScreenshotRepository(saver: ScreenshotSaver): ScreenshotRepository {
            return ScreenshotRepositoryImpl(saver)
        }

        @Provides
        @Singleton
        fun provideSaveScreenshotUseCase(screenshotRepository: ScreenshotRepository): SaveScreenshotUseCase {
            return SaveScreenshotUseCase(screenshotRepository)
        }

        @Provides
        @Singleton
        fun provideProcessScreenshotUseCase(
            saveScreenshotUseCase: SaveScreenshotUseCase,
            @Named("TextRecognition") ocrEngine: OcrEngine
        ): ProcessScreenshotUseCase {
            return ProcessScreenshotUseCase(
                saveScreenshotUseCase = saveScreenshotUseCase,
                ocrEngine = ocrEngine
            )
        }
    }
}


