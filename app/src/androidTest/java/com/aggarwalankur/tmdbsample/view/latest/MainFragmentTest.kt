package com.aggarwalankur.tmdbsample.view.latest

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.aggarwalankur.tmdbsample.R
import com.aggarwalankur.tmdbsample.data.local.MovieDao
import com.aggarwalankur.tmdbsample.data.local.MoviesDatabase
import com.aggarwalankur.tmdbsample.domain.repository.TmdbRepository
import com.aggarwalankur.tmdbsample.launchFragmentInHiltContainer
import com.aggarwalankur.tmdbsample.network.FakeMovieFactory
import com.aggarwalankur.tmdbsample.network.FakeMovieFetchService
import com.aggarwalankur.tmdbsample.network.Movie
import com.aggarwalankur.tmdbsample.network.MovieFetchService
import com.aggarwalankur.tmdbsample.saveMovieBlocking
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.any
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@MediumTest
@ExperimentalCoroutinesApi
class MainFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: MoviesDatabase

    @Inject
    lateinit var tmdbRepository: TmdbRepository

    @Inject
    lateinit var movieFetchService: MovieFetchService

    private lateinit var movieDao: MovieDao
    private lateinit var movieFactory: FakeMovieFactory

    private lateinit var movie1: Movie
    private lateinit var movie2: Movie
    private lateinit var movie3: Movie
    private lateinit var movie4: Movie

    @Before
    fun setup() {
        hiltRule.inject()

        movieDao = database.moviesDao()
        movieFactory = FakeMovieFactory()

        movie1 = movieFactory.createMovie("movie1")
        movie2 = movieFactory.createMovie("fakemovie2")
        movie3 = movieFactory.createMovie("movie3")
        movie4 = movieFactory.createMovie("movie4")

    }

    @After
    fun closeDb() = database.close()

    @Test
    fun initialViewElementsAreBlank() {

        return runTest {
            launchFragmentInHiltContainer<MainFragment>()

            onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.list)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.input_layout)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun initialListWhenNetworkFetchFails() = runTest {
        database.saveMovieBlocking(movie4)

        launchFragmentInHiltContainer<MainFragment>()

        onView(withId(R.id.list)).check(matches(isDisplayed()))

        onView(withText("movie4")).check(matches(isDisplayed()))

    }

    fun initialListWhenNetworkFetchSuccess() = runTest {
        database.saveMovieBlocking(movie4)
        Truth.assertThat(movieFetchService).isInstanceOf(FakeMovieFetchService::class.java)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie1)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie2)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie3)

        launchFragmentInHiltContainer<MainFragment>()

        onView(withId(R.id.list)).check(matches(isDisplayed()))

        onView(withText("movie1")).check(matches(isDisplayed()))
        onView(withText("fakemovie2")).check(matches(isDisplayed()))
        onView(withText("movie3")).check(matches(isDisplayed()))

        //DB is cleared when new items are fetched successfully
        onView(withText("movie4")).check(doesNotExist())
    }

    @Test
    fun errorFromRepo() {
        //Asserting that Replacement of modules works correctly
        Truth.assertThat(movieFetchService).isInstanceOf(FakeMovieFetchService::class.java)
        (movieFetchService as FakeMovieFetchService).setReturnError("fake error")

        launchFragmentInHiltContainer<MainFragment>()

        onView(withId(R.id.list)).check(matches(isDisplayed()))
            .check(matches(not(hasDescendant(any(View::class.java)))))
    }
}