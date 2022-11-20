package com.aggarwalankur.tmdbsample.data.remote

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.aggarwalankur.tmdbsample.data.local.MovieDao
import com.aggarwalankur.tmdbsample.data.local.MoviesDatabase
import com.aggarwalankur.tmdbsample.network.FakeMovieFactory
import com.aggarwalankur.tmdbsample.network.FakeMovieFetchService
import com.aggarwalankur.tmdbsample.network.Movie
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.*

@HiltAndroidTest
@SmallTest
class TmdbRemoteMediatorTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var database: MoviesDatabase
    private lateinit var movieDao: MovieDao
    private lateinit var movieFetchService: FakeMovieFetchService
    private lateinit var movieFactory: FakeMovieFactory

    private lateinit var movie1: Movie
    private lateinit var movie2: Movie
    private lateinit var movie3: Movie

    @Before
    fun setup() {
        hiltRule.inject()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MoviesDatabase::class.java
        ).allowMainThreadQueries().build()

        movieDao = database.moviesDao()
        movieFetchService = FakeMovieFetchService()
        movieFactory = FakeMovieFactory()

        movie1 = movieFactory.createMovie("movie1")
        movie2 = movieFactory.createMovie("movie2")
        movie3 = movieFactory.createMovie("movie3")
    }

    @After
    fun closeDb() = database.close()

    @ExperimentalPagingApi
    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        movieFetchService.addFakeMovie(movie1)
        movieFetchService.addFakeMovie(movie2)
        movieFetchService.addFakeMovie(movie3)

        val remoteMediator = TmdbRemoteMediator(
            "",
            movieFetchService,
            database
        )

        val pagingState = PagingState<Int, Movie>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        MatcherAssert.assertThat(result, CoreMatchers.instanceOf(RemoteMediator.MediatorResult.Success::class.java))
        Assert.assertFalse ((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @ExperimentalPagingApi
    @Test
    fun refreshLoadReturnsFailureWhenNoMoreDataIsPresent() = runTest {
        //To test this, do not enter data in fakeservice

        val remoteMediator = TmdbRemoteMediator(
            "",
            movieFetchService,
            database
        )

        val pagingState = PagingState<Int, Movie>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        MatcherAssert.assertThat(
            result,
            CoreMatchers.instanceOf(RemoteMediator.MediatorResult.Success::class.java)
        )
        Assert.assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}