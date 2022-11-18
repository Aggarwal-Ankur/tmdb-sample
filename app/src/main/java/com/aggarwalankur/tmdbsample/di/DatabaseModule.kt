package com.aggarwalankur.tmdbsample.di

import android.content.Context
import androidx.room.Room
import com.aggarwalankur.tmdbsample.data.local.MovieDao
import com.aggarwalankur.tmdbsample.data.local.MoviesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): MoviesDatabase {
        return Room
            .databaseBuilder(appContext, MoviesDatabase::class.java, MoviesDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideMovieDaoFromDatabase(moviesDatabase : MoviesDatabase) : MovieDao {
        return moviesDatabase.moviesDao()
    }
}