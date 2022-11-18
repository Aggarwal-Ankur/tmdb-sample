package com.aggarwalankur.tmdbsample.network

import retrofit2.http.GET
import retrofit2.http.Query

interface MovieFetchService {
    @GET("3/discover/movie")
    suspend fun fetchMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieList

    @GET("3/search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): MovieList
}