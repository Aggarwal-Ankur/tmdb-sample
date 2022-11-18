package com.aggarwalankur.tmdbsample.di

import com.aggarwalankur.tmdbsample.data.TmdbRepositoryImpl
import com.aggarwalankur.tmdbsample.data.local.MoviesDatabase
import com.aggarwalankur.tmdbsample.domain.repository.TmdbRepository
import com.aggarwalankur.tmdbsample.network.MovieFetchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides
    @Singleton
    fun provideTmdbRepository(service: MovieFetchService,
                              db: MoviesDatabase
    ): TmdbRepository {
        return TmdbRepositoryImpl(service, db)
    }
}