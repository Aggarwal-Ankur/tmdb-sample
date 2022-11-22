package com.aggarwalankur.tmdbsample.domain.use_case.get_latest_movies

import com.aggarwalankur.tmdbsample.domain.repository.TmdbRepository
import javax.inject.Inject

class GetLatestMoviesUseCase @Inject constructor(
    private val repository: TmdbRepository
) {
    operator fun invoke() = repository.getLatestMovies()
}