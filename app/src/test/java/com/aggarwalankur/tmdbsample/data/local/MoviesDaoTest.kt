package com.aggarwalankur.tmdbsample.data.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.aggarwalankur.tmdbsample.MainCoroutineRule
import com.aggarwalankur.tmdbsample.network.FakeMovieFactory
import com.aggarwalankur.tmdbsample.network.dto.Movie
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
@SmallTest
class MoviesDaoTest {

    //Not using HiltAndroidRule, possible error - https://stackoverflow.com/questions/62927037/customtestapplication-value-cannot-be-annotated-with-hiltandroidapp

    private lateinit var database: MoviesDatabase
    private lateinit var movieDao: MovieDao
    private lateinit var movieFactory: FakeMovieFactory

    private lateinit var movie1:Movie
    private lateinit var movie2:Movie
    private lateinit var movie3:Movie

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MoviesDatabase::class.java
        ).allowMainThreadQueries().build()

        movieDao = database.moviesDao()
        movieFactory = FakeMovieFactory()

        movie1 = movieFactory.createMovie("movie1")
        movie2 = movieFactory.createMovie("movie2")
        movie3 = movieFactory.createMovie("movie3")
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun `create and fetch single movie`() {
        //Given
        val movieList = listOf(movie1)

        return runTest{
            //When
            movieDao.insertAll(movieList)
            val fetchedPageSource = movieDao.getAllMovies()
            val pagedDataFetched = fetchedPageSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null, loadSize = 10, placeholdersEnabled = false
                ))

            val listOfFetched = (pagedDataFetched as? PagingSource.LoadResult.Page)?.data
            val fetchedMovie = listOfFetched!!.get(0)

            //Then
            assertThat(listOfFetched).hasSize(1)
            assertThat(fetchedMovie).isEqualTo(movie1)
        }
    }

    @Test
    fun `create and fetch simple list of movies`() {
        //Given
        val movieList = listOf(movie1, movie2, movie3)

        return runTest{
            //When
            movieDao.insertAll(movieList)
            val fetchedPageSource = movieDao.getAllMovies()
            val pagedDataFetched = fetchedPageSource.load(
                PagingSource.LoadParams.Refresh(
                key = null, loadSize = 10, placeholdersEnabled = false
            ))
            val listOfFetched = (pagedDataFetched as? PagingSource.LoadResult.Page)?.data

            //Then
            assertThat(listOfFetched).hasSize(3)
            assertThat(listOfFetched).isEqualTo(movieList)
        }
    }

    @Test
    fun `test fetch by id`() {
        //Given
        val movieList = listOf(movie1)
        val movieId = movie1.id

        return runTest{
            //When
            movieDao.insertAll(movieList)
            val fetchedMovie = movieDao.getMovieById(movieId = movieId)

            //Then
            assertThat(fetchedMovie).isEqualTo(movie1)
        }
    }

    @Test
    fun `test duplicate insertions with id`() {
        //Given
        val movieList = listOf(movie1, movie2, movie3)

        val key=movie1.movie_key

        val movie4 = movieFactory.createMovieWithKey("movie4", key)

        return runTest{
            //When
            movieDao.insertAll(movieList)
            movieDao.insertAll(listOf(movie4))
            val fetchedPageSource = movieDao.getAllMovies()
            val pagedDataFetched = fetchedPageSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null, loadSize = 10, placeholdersEnabled = false
                ))
            val listOfFetched = (pagedDataFetched as? PagingSource.LoadResult.Page)?.data
            val fetchedMovie = movieDao.getMovieById(movieId = movie1.id)

            //Then
            assertThat(listOfFetched).hasSize(3)
            assertThat(fetchedMovie!!.title).isEqualTo("movie4")
        }
    }

    @Test
    fun `test duplicate insertions with key`() {
        //Given
        val movieList = listOf(movie1, movie2, movie3)

        val key=movie1.movie_key
        val movie4 = movieFactory.createMovieWithKey("movie4", key)

        return runTest{
            //When
            movieDao.insertAll(movieList)
            movieDao.insertAll(listOf(movie4))
            val fetchedPageSource = movieDao.getAllMovies()
            val pagedDataFetched = fetchedPageSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null, loadSize = 10, placeholdersEnabled = false
                ))
            val listOfFetched = (pagedDataFetched as? PagingSource.LoadResult.Page)?.data
            val fetchedMovie = movieDao.getMovieByKey(key = key)

            //Then
            assertThat(listOfFetched).hasSize(3)
            assertThat(fetchedMovie!!.title).isEqualTo("movie4")
        }
    }

    @Test
    fun `test insert and clear`() {
        //Given
        val movieList = listOf(movie1, movie2, movie3)

        return runTest{
            //When
            movieDao.insertAll(movieList)
            val fetchedPageSource = movieDao.getAllMovies()

            val pagedDataFetched = fetchedPageSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null, loadSize = 10, placeholdersEnabled = false
                ))
            val listOfFetched = (pagedDataFetched as? PagingSource.LoadResult.Page)?.data

            assertThat(listOfFetched).hasSize(3)
            movieDao.clear()

            val pagedDataFetched2 = fetchedPageSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null, loadSize = 10, placeholdersEnabled = false
                ))
            val listOfFetched2 = (pagedDataFetched2 as? PagingSource.LoadResult.Page)?.data

            assertThat(listOfFetched2).hasSize(0)
        }
    }

    @Test
    fun `test getMoviesByTitle`() {
        //Given
        val movieList = listOf(movie1, movie2, movie3)

        return runTest{
            //When
            movieDao.insertAll(movieList)
            val query = "%movie%"
            val fetchedMovies = movieDao.getMoviesList()

            //Then
            assertThat(fetchedMovies).hasSize(3)
            assertThat(fetchedMovies).isEqualTo(movieList)
        }
    }
}