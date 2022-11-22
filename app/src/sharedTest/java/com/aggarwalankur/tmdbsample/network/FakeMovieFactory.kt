package com.aggarwalankur.tmdbsample.network

import java.util.concurrent.atomic.AtomicLong
import com.aggarwalankur.tmdbsample.network.dto.Movie

class FakeMovieFactory {
    private val counter = AtomicLong(0)

    fun createMovie(movieName: String): Movie {
        val movie_key = counter.incrementAndGet()
        return Movie(
            movie_key = movie_key,
            id = 1000L + movie_key,
            title = movieName,
            overview = "Movie $movieName overview",
            posterPath = "/abc$movie_key.jpg",
            releaseDate = "01-01-2020",
            voteAverage = 100.100,
            voteCount = 2000,
            popularity = 200.202
        )
    }

    fun createMovieWithKey(movieName: String, movie_key : Long): Movie {
        return Movie(
            movie_key = movie_key,
            id = 1000L + movie_key,
            title = movieName,
            overview = "Movie $movieName overview",
            posterPath = "/abc$movie_key.jpg",
            releaseDate = "01-01-2000",
            voteAverage = 100.100,
            voteCount = 2000,
            popularity = 200.200
        )
    }
}