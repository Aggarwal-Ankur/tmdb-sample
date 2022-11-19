package com.aggarwalankur.tmdbsample.network

import retrofit2.http.Query
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min

class FakeMovieFetchService  @Inject constructor() : MovieFetchService {

    var failureMsg: String? = null
    private val items : MutableList<Movie> = arrayListOf()

    fun addFakeMovie(movie: Movie){
        items.add(movie)
    }

    override suspend fun fetchMovies(apiKey: String, page: Int): MovieList {
        failureMsg?.let {
            throw IOException(it)
        }

        val returnItems = getMovies(page)

        return MovieList(
            items = returnItems,
            totalCount = returnItems.count(),
            nextPage = if (returnItems.count() < 20) null else page+1
        )
    }

    override suspend fun searchMovies(apiKey: String, query: String, page: Int): MovieList {
        TODO("Not yet implemented")
    }


    private fun getMovies(page:Int) : List<Movie> {
        //TMDB repo is 1-based
        if (page<1) {
            return items.subList(0, min(items.size, 20))
        }
        val startPos = (page-1) * 20
        return items.subList(startPos, min(items.size, startPos + 20))
    }
}