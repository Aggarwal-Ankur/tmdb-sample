package com.aggarwalankur.tmdbsample.view.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aggarwalankur.tmdbsample.domain.use_case.get_latest_movies.GetLatestMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getLatestMoviesUseCase: GetLatestMoviesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val pagingDataFlow = getLatestMoviesUseCase().cachedIn(viewModelScope)
}