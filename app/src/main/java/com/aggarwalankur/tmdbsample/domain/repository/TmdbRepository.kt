package com.aggarwalankur.tmdbsample.domain.repository

import androidx.paging.PagingData
import com.aggarwalankur.tmdbsample.network.Movie
import kotlinx.coroutines.flow.Flow

interface TmdbRepository {
    suspend fun getLatestMovies() : Flow<PagingData<Movie>>

    suspend fun getSearchFilteredMovies(searchString : String) : Flow<PagingData<Movie>>
}