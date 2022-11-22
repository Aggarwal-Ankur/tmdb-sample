package com.aggarwalankur.tmdbsample.network

import com.aggarwalankur.tmdbsample.common.Constants
import okhttp3.internal.filterList
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min

class FakeMovieFetchService  @Inject constructor() : MovieFetchService {

    var failureMsg: String? = null
    var defaultPageSize:Int = Constants.NETWORK_PAGE_SIZE
    private val items : MutableList<Movie> = arrayListOf()

    fun addFakeMovie(movie: Movie){
        items.add(movie)
    }

    fun setPageSize (pageSize : Int) {
        defaultPageSize = pageSize
    }

    fun setReturnError(value: String) {
        failureMsg = value
    }

    fun clearError(value: String) {
        failureMsg = null
    }

    override suspend fun fetchMovies(apiKey: String, page: Int): MovieList {
        failureMsg?.let {
            throw IOException(it)
        }

        val returnItems = getMovies(page)

        return MovieList(
            items = returnItems,
            totalCount = returnItems.count(),
            nextPage = if (returnItems.count() < defaultPageSize) null else page+1
        )
    }

    override suspend fun searchMovies(apiKey: String, query: String, page: Int): MovieList {
        failureMsg?.let {
            throw IOException(it)
        }
        val filteredItems = items.filterList { this.title.contains(query) }
        return MovieList(items = filteredItems, totalCount = filteredItems.size)
    }


    private fun getMovies(page:Int) : List<Movie> {
        //TMDB repo is 1-based
        if (page<1) {
            return items.subList(0, min(items.size, defaultPageSize))
        }
        val startPos = (page-1) * defaultPageSize
        if (items.size < startPos) return emptyList()
        return items.subList(startPos, min(items.size, startPos + defaultPageSize))
    }
}