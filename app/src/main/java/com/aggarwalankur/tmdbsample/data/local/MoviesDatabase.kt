package com.aggarwalankur.tmdbsample.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aggarwalankur.tmdbsample.network.Movie

@Database( entities = [Movie::class, RemoteKeys::class],
    version = 1, exportSchema = false )
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun moviesDao() : MovieDao
    abstract fun remoteKeysDao() : RemoteKeysDao

    companion object {
        val DB_NAME = "tmdb.db"
    }
}