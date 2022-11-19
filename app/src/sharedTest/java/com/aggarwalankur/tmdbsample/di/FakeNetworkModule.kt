package com.aggarwalankur.tmdbsample.di

import com.aggarwalankur.tmdbsample.network.FakeMovieFetchService
import com.aggarwalankur.tmdbsample.network.MovieFetchService
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
abstract class FakeNetworkModule {
    @Singleton
    @Binds
    abstract fun bindTmdbService(movieFetchService: FakeMovieFetchService): MovieFetchService
}