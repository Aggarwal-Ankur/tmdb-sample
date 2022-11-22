package com.aggarwalankur.tmdbsample.view.searchresults

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
import com.aggarwalankur.tmdbsample.launchFragmentInHiltContainer
import com.aggarwalankur.tmdbsample.network.FakeMovieFactory
import com.aggarwalankur.tmdbsample.network.FakeMovieFetchService
import com.aggarwalankur.tmdbsample.network.Movie
import com.aggarwalankur.tmdbsample.network.MovieFetchService
import com.aggarwalankur.tmdbsample.saveMovieBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.any
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@MediumTest
@ExperimentalCoroutinesApi
class SearchResultsFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: MoviesDatabase

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
        val bundle = SearchResultsFragmentArgs(searchQuery = "").toBundle()

        return runTest {
            launchFragmentInHiltContainer<SearchResultsFragment>(bundle)

            onView(withId(R.id.list)).check(matches(isDisplayed()))
                .check(matches(not(hasDescendant(any(View::class.java)))))
            onView(withId(R.id.searchCount)).check(matches(isDisplayed()))

            val errorText = "No results found for search criteria "

            onView(withId(R.id.searchCount)).check(matches(withText(errorText)))
        }
    }

    @Test
    fun errorDisplayedWhenNetworkFetchFails() = runTest {
        database.saveMovieBlocking(movie4)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie1)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie2)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie3)
        (movieFetchService as FakeMovieFetchService).setReturnError("fake error")

        val bundle = SearchResultsFragmentArgs(searchQuery = "movie").toBundle()
        launchFragmentInHiltContainer<SearchResultsFragment>(bundle)

        onView(withId(R.id.list)).check(matches(isDisplayed()))

        //Movie 1,2,3 are supposed to come from Fetch Service, but do not since there was an error
        onView(withId(R.id.searchCount)).check(matches(isDisplayed()))

        val errorText = "No results found for search criteria movie"

        onView(withId(R.id.searchCount)).check(matches(withText(errorText)))

    }

    @Test
    fun searchListWhenNetworkFetchSuccess() = runTest {
        database.saveMovieBlocking(movie4)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie1)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie2)
        (movieFetchService as FakeMovieFetchService).addFakeMovie(movie3)

        val bundle = SearchResultsFragmentArgs(searchQuery = "movie").toBundle()
        launchFragmentInHiltContainer<SearchResultsFragment>(bundle)

        onView(withId(R.id.list)).check(matches(isDisplayed()))

        onView(withText(movie1.title)).check(matches(isDisplayed()))
        onView(withText(movie2.title)).check(matches(isDisplayed()))
        onView(withText(movie3.title)).check(matches(isDisplayed()))

        val searchText = "Showing top 3 of 3 results for search criteria movie"
        onView(withId(R.id.searchCount)).check(matches(withText(searchText)))

        //DB does not effect our search
        onView(withText(movie4.title)).check(doesNotExist())
    }


}