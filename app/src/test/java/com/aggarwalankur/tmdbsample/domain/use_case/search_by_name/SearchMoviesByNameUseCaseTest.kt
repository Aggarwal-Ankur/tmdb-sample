package com.aggarwalankur.tmdbsample.domain.use_case.search_by_name

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.*
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

    private lateinit var searchMoviesByNameUseCase: SearchMoviesByNameUseCase
    private lateinit var movieFactory: FakeMovieFactory

    private lateinit var movie1: Movie
    private lateinit var movie2: Movie
    private lateinit var movie3: Movie
    private lateinit var movie4: Movie

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
        movie2 = movieFactory.createMovie("mofake2")
        movie3 = movieFactory.createMovie("movie3")
        movie4 = movieFactory.createMovie("movie4")

        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie1)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie2)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie3)


        tmdbRepository = TmdbRepositoryImpl(service = movieFetchService, db = database)
        searchMoviesByNameUseCase = SearchMoviesByNameUseCase(tmdbRepository)
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun `test MovieFetchService returns a single item`() = runTest {
        //When
        val data = searchMoviesByNameUseCase("movie")

        //Then
        data.test{
            val received1 = awaitItem()
            val received2 = awaitItem()
            awaitComplete()
            assertTrue(received1 is Resource.Loading)
            assertTrue(received2 is Resource.Success)
            assertThat(received2.data!!.items).contains(movie1)
            assertThat(received2.data!!.items).contains(movie3)
            assertThat(received2.data!!.items).doesNotContain(movie2)
            assertThat(received2.data!!.totalCount).isEqualTo(2)
        }

    }

    @Test
    fun `test MovieFetchService not affected by database`() = runTest {
        //Given
        database.saveMovieBlocking(movie4)

        //When
        val data = searchMoviesByNameUseCase("movie")

        //Then
        data.test{
            val received1 = awaitItem()
            val received2 = awaitItem()
            awaitComplete()
            assertTrue(received1 is Resource.Loading)
            assertTrue(received2 is Resource.Success)
            assertThat(received2.data!!.items).contains(movie1)
            assertThat(received2.data!!.items).contains(movie3)
            assertThat(received2.data!!.items).doesNotContain(movie4)
            assertThat(received2.data!!.totalCount).isEqualTo(2)
        }

    }

    @Test
    fun `received a consolidated list for staggered insertions`() = runTest {
        //Given
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie4)

        //When
        val data = searchMoviesByNameUseCase("movie")

        //Then
        data.test{
            val received1 = awaitItem()
            val received2 = awaitItem()
            awaitComplete()
            assertTrue(received1 is Resource.Loading)
            assertTrue(received2 is Resource.Success)
            assertThat(received2.data!!.items).contains(movie1)
            assertThat(received2.data!!.items).contains(movie3)
            assertThat(received2.data!!.items).contains(movie4)
            assertThat(received2.data!!.totalCount).isEqualTo(3)
        }

    }

    @Test
    fun `received error when MovieFetchService returns error`() = runTest {
        //Given
        (movieFetchService as FakeMovieFetchService).setReturnError("fetch error")

        //When
        val data = searchMoviesByNameUseCase("movie")

        //Then
        data.test{
            val received1 = awaitItem()
            val received2 = awaitItem()
            awaitComplete()
            assertTrue(received1 is Resource.Loading)
            assertTrue(received2 is Resource.Error)
            assertThat(received2.data).isNull()
            assertThat(received2.message).isEqualTo("fetch error")

        }

    }

}