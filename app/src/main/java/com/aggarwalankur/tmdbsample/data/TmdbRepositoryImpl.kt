package com.aggarwalankur.tmdbsample.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.aggarwalankur.tmdbsample.data.local.MoviesDatabase
import com.aggarwalankur.tmdbsample.data.remote.TmdbRemoteMediator
import com.aggarwalankur.tmdbsample.domain.repository.TmdbRepository
import com.aggarwalankur.tmdbsample.network.MovieFetchService
import javax.inject.Inject

class TmdbRepositoryImpl @Inject constructor(
    private val service: MovieFetchService,
    private val db: MoviesDatabase
) : TmdbRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getLatestMovies() = Pager(
        config = PagingConfig(NETWORK_PAGE_SIZE),
        remoteMediator = TmdbRemoteMediator("", service, db)
    ) {
        db.moviesDao().getAllMovies()
    }.flow

    @OptIn(ExperimentalPagingApi::class)
    override fun getSearchFilteredMovies(searchString: String) = Pager(
        config = PagingConfig(NETWORK_PAGE_SIZE),
        remoteMediator = TmdbRemoteMediator(searchString, service, db)
    ) {
        db.moviesDao().getAllMovies()
    }.flow


    companion object {
        //TMDB page size is always 20
        const val NETWORK_PAGE_SIZE = 20
    }

}