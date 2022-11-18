package com.aggarwalankur.tmdbsample.network

import com.google.gson.annotations.SerializedName

data class MovieList (
    @field:SerializedName("results")
    val items: List<Movie> = emptyList(),

    @field:SerializedName("total_results") val totalCount: Int,

    val nextPage: Int? = null
)
