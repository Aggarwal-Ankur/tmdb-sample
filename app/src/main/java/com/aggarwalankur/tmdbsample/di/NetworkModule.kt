package com.aggarwalankur.tmdbsample.di

import com.aggarwalankur.tmdbsample.BuildConfig
import com.aggarwalankur.tmdbsample.common.Constants
import com.aggarwalankur.tmdbsample.network.MovieFetchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideTmdbService(): MovieFetchService {
        val clientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.level = Level.BASIC
            clientBuilder.addInterceptor(logger)
        }
        val client = clientBuilder.build()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieFetchService::class.java)
    }
}