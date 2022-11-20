package com.aggarwalankur.tmdbsample

import com.aggarwalankur.tmdbsample.data.local.MoviesDatabase
import com.aggarwalankur.tmdbsample.network.Movie
import kotlinx.coroutines.runBlocking

fun MoviesDatabase.saveMovieBlocking(movie: Movie) = runBlocking {
    this@saveMovieBlocking.moviesDao().insertAll(listOf(movie))
}

fun MoviesDatabase.saveMovieBlocking(movieList : List<Movie>) = runBlocking {
    this@saveMovieBlocking.moviesDao().insertAll(movieList)
}

