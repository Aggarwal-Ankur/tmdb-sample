package com.aggarwalankur.tmdbsample.view.latest

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import com.aggarwalankur.tmdbsample.R
import com.aggarwalankur.tmdbsample.TMDBSampleApp
import com.aggarwalankur.tmdbsample.databinding.FragmentMainBinding
import com.aggarwalankur.tmdbsample.network.Movie
import com.aggarwalankur.tmdbsample.view.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : Fragment(), ItemViewHolder.OnClickListener {
    lateinit var binding: FragmentMainBinding
        private set

    private lateinit var adapter: MoviePageBindingAdapter

    private val viewModel: MainViewModel by viewModels()

    private lateinit var searchView : AutoCompleteTextView

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

        searchView = binding.searchTV
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
        adapter = MoviePageBindingAdapter(this@MainFragment)
        list.adapter = adapter.withLoadStateFooter(
            footer = MainLoadStateAdapter { adapter.retry() }
        )

        bindList(
            adapter = adapter,
            pagingData = pagingData
        )

        initSearchTV()

    }

    private fun FragmentMainBinding.bindList(
        adapter: MoviePageBindingAdapter,
        pagingData: Flow<PagingData<Movie>>
    ) {
        retryButton.setOnClickListener { adapter.retry() }
        swipeLayout.setOnRefreshListener {
            swipeLayout.isRefreshing = false
            adapter.retry()
        }

        searchView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val searchString=searchView.adapter.getItem(position)
                Timber.d("clicked $searchString")
            }
        }

        val arrayAdapter = ArrayAdapter<String>(TMDBSampleApp.instance, android.R.layout.simple_dropdown_item_1line)
        searchView.setAdapter(arrayAdapter)

        lifecycleScope.launchWhenCreated {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.savedMovies.collectLatest { data ->
                if (!data.isEmpty()) {
                    arrayAdapter.clear()
                    Timber.d("++++ main fragment size = ${data.size}")
                    arrayAdapter.addAll(data)
                    arrayAdapter.notifyDataSetChanged()
                }
            }
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
                        && adapter.itemCount == 0
                // Snackbar on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    val snackbar = Snackbar.make(binding.mainLayout,
                        getString(R.string.default_network_error), Snackbar.LENGTH_INDEFINITE)
                    snackbar.setAction("OK", View.OnClickListener {
                        snackbar.dismiss()
                    })

                    snackbar.show()
                }
            }
        }
    }

    private fun initSearchTV() {
        binding.searchTV.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                movieSearched()
                true
            } else {
                false
            }
        }
        binding.searchTV.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                movieSearched()
                true
            } else {
                false
            }
        }
    }

    private fun movieSearched() {
        binding.searchTV.text.trim().toString().let {
            if (it.isNotBlank()) {
                searchAction(it)
            }
        }
    }

    private fun searchAction(searchString : String) {
        val action = MainFragmentDirections.navigateToSearchResults(searchString)
        findNavController().navigate(action)
    }


}