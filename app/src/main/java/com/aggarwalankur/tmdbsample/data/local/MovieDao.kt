package com.aggarwalankur.tmdbsample.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aggarwalankur.tmdbsample.network.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<Movie>)

    @Query("DELETE FROM movies")
    suspend fun clear()

    @Query("SELECT * FROM movies")
    fun getAllMovies(): PagingSource<Int, Movie>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Long): Movie?

    @Query("SELECT * FROM movies WHERE movie_key = :key")
    suspend fun getMovieByKey(key: Long): Movie?

    @Query("SELECT * FROM movies WHERE title LIKE :queryString")
    fun getMoviesByTitle(queryString : String): List<Movie>
}