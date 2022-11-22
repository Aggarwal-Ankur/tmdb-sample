package com.aggarwalankur.tmdbsample.domain.repository

import androidx.paging.PagingData
import com.aggarwalankur.tmdbsample.network.dto.Movie
import com.aggarwalankur.tmdbsample.network.dto.MovieList
import kotlinx.coroutines.flow.Flow

interface TmdbRepository {
    fun getLatestMovies(): Flow<PagingData<Movie>>

    suspend fun getSavedMovies(): List<Movie>

    suspend fun getSearchFilteredMovies(searchString: String): MovieList
}