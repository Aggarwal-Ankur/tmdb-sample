package com.aggarwalankur.tmdbsample.di

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
    const val BASE_URL = "https://api.themoviedb.org/"
    const val IMAGE_URL = "https://image.tmdb.org/t/p/w200"

    @Singleton
    @Provides
    fun provideTmdbService(): MovieFetchService {
        val logger = HttpLoggingInterceptor()
        logger.level = Level.BASIC
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieFetchService::class.java)
    }
}