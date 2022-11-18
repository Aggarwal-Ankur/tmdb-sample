package com.aggarwalankur.tmdbsample.view.latest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import com.aggarwalankur.tmdbsample.databinding.FragmentMainBinding
import com.aggarwalankur.tmdbsample.network.Movie
import com.aggarwalankur.tmdbsample.view.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class MainFragment : Fragment(), ItemViewHolder.OnClickListener {
    lateinit var binding: FragmentMainBinding
        private set

    private lateinit var adapter: MovieBindingAdapter

    private val viewModel: MainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)


        // bind the state
        binding.bindState(
            pagingData = viewModel.pagingDataFlow
        )

        return binding.root
    }

    override fun onMovieClick(movie: Movie) {
        val action = MainFragmentDirections.navigateToDetailsFragment(movie)
        findNavController().navigate(action)
    }

    private fun FragmentMainBinding.bindState(
        pagingData: Flow<PagingData<Movie>>
    ) {
        adapter = MovieBindingAdapter(this@MainFragment)
        list.adapter = adapter.withLoadStateFooter(
            footer = MainLoadStateAdapter { adapter.retry() }
        )
        /*bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )*/
        bindList(
            adapter = adapter,
            pagingData = pagingData
        )
    }

    private fun FragmentMainBinding.bindList(
        adapter: MovieBindingAdapter,
        pagingData: Flow<PagingData<Movie>>
    ) {
        retryButton.setOnClickListener { adapter.retry() }
        swipeLayout.setOnRefreshListener {
            swipeLayout.isRefreshing = false
            adapter.retry()
        }

        val notLoading = adapter.loadStateFlow
            // Only emit when REFRESH LoadState for RemoteMediator changes.
            .distinctUntilChangedBy { it.source.refresh }
            // Only react to cases where Remote REFRESH completes i.e., NotLoading.
            .map { it.source.refresh is LoadState.NotLoading }


        lifecycleScope.launchWhenCreated {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collect { loadState ->

                val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                // show empty list
                emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                list.isVisible = !isListEmpty
                // Show loading spinner during initial load or refresh.
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error
                        && adapter.itemCount == 0 && searchUser.text.trim().length > 0
                // Snackbar on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    val snackbar = Snackbar.make(binding.mainLayout, "Error String", Snackbar.LENGTH_INDEFINITE)
                    snackbar.setAction("OK", View.OnClickListener {
                        snackbar.dismiss()
                    })

                    snackbar.show()
                }
            }
        }
    }


}