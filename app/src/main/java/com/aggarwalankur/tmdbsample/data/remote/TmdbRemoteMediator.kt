package com.aggarwalankur.tmdbsample.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aggarwalankur.tmdbsample.BuildConfig
import com.aggarwalankur.tmdbsample.data.local.MoviesDatabase
import com.aggarwalankur.tmdbsample.data.local.RemoteKeys
import com.aggarwalankur.tmdbsample.network.Movie
import com.aggarwalankur.tmdbsample.network.MovieFetchService
import retrofit2.HttpException
import java.io.IOException

//TMDB page values start at 1, not 0
private const val TMDB_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class TmdbRemoteMediator(
    private val query: String,
    private val service: MovieFetchService,
    private val movieDatabase: MoviesDatabase
) : RemoteMediator<Int, Movie>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Movie>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: TMDB_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }

        //if (query.isBlank()) return MediatorResult.Error(Exception())

        try {
            val apiResponse = service.fetchMovies(BuildConfig.API_KEY, page)

            val movies = apiResponse.items
            val endOfPaginationReached = movies.isEmpty()
            movieDatabase.withTransaction {
                // clear all data for movies
                if (loadType == LoadType.REFRESH && !movies.isEmpty()) {
                    movieDatabase.remoteKeysDao().clearRemoteKeys()
                    movieDatabase.moviesDao().clear()
                }
                val prevKey = if (page == TMDB_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = movies.map {
                    RemoteKeys(movieId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                movieDatabase.remoteKeysDao().insertAll(keys)
                movieDatabase.moviesDao().insertAll(movies)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Movie>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { movie ->
                // Get the remote keys of the last item retrieved
                movieDatabase.remoteKeysDao().remoteKeysUserId(movie.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Movie>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { movie ->
                // Get the remote keys of the first items retrieved
                movieDatabase.remoteKeysDao().remoteKeysUserId(movie.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Movie>
    ): RemoteKeys? {
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { movieId ->
                movieDatabase.remoteKeysDao().remoteKeysUserId(movieId)
            }
        }
    }
}