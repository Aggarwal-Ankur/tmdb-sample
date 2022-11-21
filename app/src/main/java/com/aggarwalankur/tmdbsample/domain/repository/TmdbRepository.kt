package com.aggarwalankur.tmdbsample.domain.repository

import androidx.paging.PagingData
import com.aggarwalankur.tmdbsample.network.Movie
import com.aggarwalankur.tmdbsample.network.MovieList
import kotlinx.coroutines.flow.Flow

interface TmdbRepository {
    fun getLatestMovies() : Flow<PagingData<Movie>>

    suspend fun getSavedMovies() : List<Movie>

    suspend fun getSearchFilteredMovies(searchString : String) : MovieList
}