package com.aggarwalankur.tmdbsample.view.details

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.aggarwalankur.tmdbsample.launchFragmentInHiltContainer
import com.aggarwalankur.tmdbsample.network.FakeMovieFactory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import com.aggarwalankur.tmdbsample.R
import org.junit.Before
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@MediumTest
@ExperimentalCoroutinesApi
class DetailsFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val moviesFactory = FakeMovieFactory()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun selectedMovieIsDisplayed() {
        val movieName = "Fake Movie"
        val movie = moviesFactory.createMovie(movieName)
        val bundle = DetailsFragmentArgs(selectedMovie = movie).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(bundle)

        onView(withId(R.id.posterIv)).check(matches(isDisplayed()))
        onView(withId(R.id.titleTv)).check(matches(withText(movieName)))
        onView(withId(R.id.overviewTv)).check(matches(withText("Movie $movieName overview")))
        onView(withId(R.id.releaseDateTv)).check(matches(withText("Release Date : 01-01-2020")))
        onView(withId(R.id.voteCountTv)).check(matches(withText("Vote Count : 2000")))
        onView(withId(R.id.popularityTv)).check(matches(withText("Popularity : 200.202")))

    }
}