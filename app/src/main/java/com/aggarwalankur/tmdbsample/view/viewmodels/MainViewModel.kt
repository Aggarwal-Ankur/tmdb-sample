package com.aggarwalankur.tmdbsample.view.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aggarwalankur.tmdbsample.common.Resource
import com.aggarwalankur.tmdbsample.domain.use_case.get_latest_movies.GetLatestMoviesUseCase
import com.aggarwalankur.tmdbsample.domain.use_case.get_saved_movies.GetSavedMoviesUseCase
import com.aggarwalankur.tmdbsample.domain.use_case.search_by_name.SearchMoviesByNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getLatestMoviesUseCase: GetLatestMoviesUseCase,
    private val getSavedMoviesUseCase: GetSavedMoviesUseCase
) : ViewModel() {

    val pagingDataFlow = getLatestMoviesUseCase().cachedIn(viewModelScope)

    private val _savedMovies = MutableStateFlow(listOf<String>())
    val savedMovies = _savedMovies.asStateFlow()

    init {
        //Equivalent to launchIn(viewModelScope)
        viewModelScope.launch {
            pagingDataFlow.collectLatest {
                getMovies()
            }
        }
    }

    private fun getMovies() {
        getSavedMoviesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        _savedMovies.value = it.map { movie ->
                            movie.title
                        }
                    }
                }
            }

        }.launchIn(viewModelScope)
    }

}