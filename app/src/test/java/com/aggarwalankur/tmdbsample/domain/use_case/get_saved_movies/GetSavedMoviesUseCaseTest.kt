package com.aggarwalankur.tmdbsample.domain.use_case.get_saved_movies

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import app.cash.turbine.testIn
import com.aggarwalankur.tmdbsample.MainCoroutineRule
import com.aggarwalankur.tmdbsample.common.Resource
import com.aggarwalankur.tmdbsample.data.TmdbRepositoryImpl
import com.aggarwalankur.tmdbsample.data.local.MoviesDatabase
import com.aggarwalankur.tmdbsample.domain.repository.TmdbRepository
import com.aggarwalankur.tmdbsample.network.FakeMovieFactory
import com.aggarwalankur.tmdbsample.network.FakeMovieFetchService
import com.aggarwalankur.tmdbsample.network.Movie
import com.aggarwalankur.tmdbsample.network.MovieFetchService
import com.aggarwalankur.tmdbsample.saveMovieBlocking
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P], application = HiltTestApplication::class)
@SmallTest
class GetLatestMoviesUseCaseTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: MoviesDatabase

    @Inject
    lateinit var movieFetchService: MovieFetchService

    @Inject
    lateinit var tmdbRepository: TmdbRepository

    private lateinit var getSavedMoviesUseCase: GetSavedMoviesUseCase
    private lateinit var movieFactory: FakeMovieFactory

    private lateinit var movie1: Movie
    private lateinit var movie2: Movie
    private lateinit var movie3: Movie

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        hiltRule.inject()

        movieFactory = FakeMovieFactory()

        movie1 = movieFactory.createMovie("movie1")
        movie2 = movieFactory.createMovie("movie2")
        movie3 = movieFactory.createMovie("movie3")
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie1)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie2)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie3)

        tmdbRepository = TmdbRepositoryImpl(service = movieFetchService, db = database)
        getSavedMoviesUseCase = GetSavedMoviesUseCase(tmdbRepository)
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun `create and fetch single movie`() = runTest {
        //Given
        val movieList = listOf(movie1)
        //When
        database.saveMovieBlocking(movieList)
        val data = getSavedMoviesUseCase()

        //Then
        data.test{
            val received1 = awaitItem()
            val received2 = awaitItem()
            awaitComplete()
            assertTrue(received1 is Resource.Loading)
            assertTrue(received2 is Resource.Success)
            assertThat(received2.data).isEqualTo(movieList)
        }

    }

    @Test
    fun `received a consolidated list when multiple movie lists inserted`() = runTest {
        //Given
        val movieList1 = listOf(movie1, movie2)
        val movieList2 = listOf(movie2, movie3)

        val expected = listOf(movie1, movie2, movie3)
        //When
        database.saveMovieBlocking(movieList1)
        database.saveMovieBlocking(movieList2)

        val data = getSavedMoviesUseCase()

        //Then
        data.test{
            val received1 = awaitItem()
            val received2 = awaitItem()
            awaitComplete()
            assertTrue(received1 is Resource.Loading)
            assertTrue(received2 is Resource.Success)
            assertThat(received2.data).isEqualTo(expected)
        }
    }

    @Test
    fun `received a consolidated list for staggered insertions`() = runTest {
        //Given
        val movieList1 = listOf(movie1, movie2)
        val movieList2 = listOf(movie2, movie3)

        val expected = listOf(movie1, movie2, movie3) //combined list
        //When
        database.saveMovieBlocking(movieList1)


        val data1 = getSavedMoviesUseCase().testIn(backgroundScope)

        database.moviesDao().insertAll(movieList2)

        val data2 = getSavedMoviesUseCase().testIn(backgroundScope)

        val received1 = data1.awaitItem()
        val received2 = data1.awaitItem()
        val received3 = data2.awaitItem()
        val received4 = data2.awaitItem()

        assertTrue(received1 is Resource.Loading)
        assertTrue(received2 is Resource.Success)
        assertThat(received2.data).isEqualTo(movieList1)

        assertTrue(received3 is Resource.Loading)
        assertTrue(received4 is Resource.Success)
        assertThat(received4.data).isEqualTo(expected)


    }

}