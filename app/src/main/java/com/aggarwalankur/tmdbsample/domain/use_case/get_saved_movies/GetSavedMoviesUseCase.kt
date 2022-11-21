package com.aggarwalankur.tmdbsample.domain.use_case.get_saved_movies

import com.aggarwalankur.tmdbsample.common.Resource
import com.aggarwalankur.tmdbsample.domain.repository.TmdbRepository
import com.aggarwalankur.tmdbsample.network.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.CancellationException
import javax.inject.Inject

class GetSavedMoviesUseCase @Inject constructor(
    private val repository: TmdbRepository
){
    operator fun invoke() : Flow<Resource<List<Movie>>> = flow {
        try {
            Timber.d("++++ saved 1")
            emit(Resource.Loading<List<Movie>>())
            val movies = repository.getSavedMovies()
            Timber.d("++++ saved 2. size = ${movies.size}")
            emit(Resource.Success<List<Movie>>(movies))
        } catch (e : CancellationException) {
            emit(Resource.Error<List<Movie>>("Movie Fetch cancelled"))
        } catch (e : Exception) {
            emit(Resource.Error<List<Movie>>(e.localizedMessage ?: "An unexpected error occured"))
        }
    }
}