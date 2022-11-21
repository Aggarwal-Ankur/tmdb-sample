package com.aggarwalankur.tmdbsample.view.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aggarwalankur.tmdbsample.R
import com.aggarwalankur.tmdbsample.common.Resource
import com.aggarwalankur.tmdbsample.common.Strings
import com.aggarwalankur.tmdbsample.domain.use_case.search_by_name.SearchMoviesByNameUseCase
import com.aggarwalankur.tmdbsample.network.MovieList
import com.aggarwalankur.tmdbsample.view.searchresults.SearchResultsFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    private val searchMoviesByNameUseCase: SearchMoviesByNameUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchedMovies = MutableStateFlow(MovieList())
    val searchedMovies = _searchedMovies.asStateFlow()

    private val _searchedMoviesError = MutableStateFlow(String())
    val searchedMoviesError = _searchedMoviesError.asStateFlow()

    private val args = SearchResultsFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val searchString = args.searchQuery

    init {
        //Equivalent to launchIn(viewModelScope)
        viewModelScope.launch {

            getSearchResults()
        }
    }

    fun getSearchResults() {
        searchMoviesByNameUseCase(searchString).onEach { result ->
            when(result) {
                is Resource.Success -> {
                    Timber.d("++++ Searched size = ${result.data?.items?.size}")
                    result.data?.let{
                        _searchedMovies.value = it
                        _searchedMoviesError.value = Strings.get(R.string.empty) //Clear any previous errors
                    }
                }

                is Resource.Loading -> {
                    //Do nothing
                }

                is Resource.Error -> {
                    _searchedMoviesError.value = Strings.get(R.string.default_network_error)
                }
            }

        }.launchIn(viewModelScope)
    }



}