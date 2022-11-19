package com.aggarwalankur.tmdbsample.network

import java.util.concurrent.atomic.AtomicLong

class FakeMovieFactory {
    private val counter = AtomicLong(0)

    fun createMovie(movieName: String): Movie {
        val id = counter.incrementAndGet()
        return Movie(
            movie_key = id,
            id = 1000L + id,
            title = movieName,
            overview = "Movie $movieName overview",
            posterPath = "/abc$id.jpg",
            releaseDate = "01-01-2000",
            voteAverage = 100.100,
            voteCount = 2000,
            popularity = 200.200
        )
    }
}