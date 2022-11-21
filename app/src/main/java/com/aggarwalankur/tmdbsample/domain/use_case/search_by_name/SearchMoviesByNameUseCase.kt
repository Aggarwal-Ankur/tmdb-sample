package com.aggarwalankur.tmdbsample.domain.use_case.search_by_name

import com.aggarwalankur.tmdbsample.common.Resource
import com.aggarwalankur.tmdbsample.domain.repository.TmdbRepository
import com.aggarwalankur.tmdbsample.network.Movie
import com.aggarwalankur.tmdbsample.network.MovieList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.CancellationException
import javax.inject.Inject

class SearchMoviesByNameUseCase @Inject constructor(
    private val repository: TmdbRepository
){
    operator fun invoke(queryString : String) : Flow<Resource<MovieList>> = flow {
        try {
            emit(Resource.Loading<MovieList>())
            Timber.d("++++ search 1.")
            val movieList = repository.getSearchFilteredMovies(queryString)
            Timber.d("++++ search 2. size = ${movieList.items.size}")
            emit(Resource.Success<MovieList>(movieList))
        } catch (e : CancellationException) {
            emit(Resource.Error<MovieList>("Movie Fetch cancelled"))
        } catch (e : Exception) {
            emit(Resource.Error<MovieList>(e.localizedMessage ?: "An unexpected error occured"))
        }
    }
}