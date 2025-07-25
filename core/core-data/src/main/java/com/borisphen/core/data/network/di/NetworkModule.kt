package com.borisphen.core.data.network.di

import com.borisphen.core.data.network.AuthInterceptor
import com.borisphen.core.data.network.service.GroqApiService
import com.borisphen.core.domain.config.ApiConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(appConfig: ApiConfig): AuthInterceptor =
        AuthInterceptor(appConfig.apiKey)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        appConfig: ApiConfig,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(appConfig.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    moshi
//                    Moshi.Builder()
//                        .add(KotlinJsonAdapterFactory())
//                        .build()
                )
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideGroqApiService(retrofit: Retrofit): GroqApiService {
        return retrofit.create(GroqApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

//    companion object {
//        const val BASE_URL = "https://api.groq.com/"
//    }
}