package com.aggarwalankur.tmdbsample.view.latest

import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.aggarwalankur.tmdbsample.R
import com.aggarwalankur.tmdbsample.databinding.FragmentMainBinding
import com.aggarwalankur.tmdbsample.network.dto.Movie
import com.aggarwalankur.tmdbsample.view.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainFragment : Fragment(), ItemViewHolder.OnClickListener {
    lateinit var binding: FragmentMainBinding
        private set

    private lateinit var adapter: MoviePageBindingAdapter
    private lateinit var searchAdapter: ArrayAdapter<String>

    private val viewModel: MainViewModel by viewModels()

    private lateinit var searchView: AutoCompleteTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)


        if (Configuration.ORIENTATION_PORTRAIT == resources.configuration.orientation) {
            binding.list.layoutManager = GridLayoutManager(activity, 2)
        } else {
            binding.list.layoutManager = GridLayoutManager(activity, 3)
        }

        searchView = binding.searchTV
        bindAdapter()

        bindList(
            adapter = adapter,
            pagingData = viewModel.pagingDataFlow
        )
        initSearchTV()
        return binding.root
    }

    override fun onMovieClick(movie: Movie) {
        val action = MainFragmentDirections.navigateToDetailsFragment(movie)
        findNavController().navigate(action)
    }

    private fun bindAdapter() {
        adapter = MoviePageBindingAdapter(this@MainFragment)
        binding.list.adapter = adapter

    }

    private fun bindList(
        adapter: MoviePageBindingAdapter,
        pagingData: Flow<PagingData<Movie>>
    ) {
        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            adapter.retry()
        }


        activity?.let {
            searchAdapter = ArrayAdapter<String>(
                it.applicationContext,
                android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1
            )
            searchView.setAdapter(searchAdapter)
        }


        lifecycleScope.launchWhenCreated {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.savedMovies.collectLatest { data ->
                if (!data.isEmpty()) {
                    searchAdapter.clear()
                    searchAdapter.addAll(data)
                    searchAdapter.notifyDataSetChanged()
                }
            }
        }



        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collect { loadState ->

                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                // Only show the list if refresh succeeds.
                binding.list.isVisible = !isListEmpty
                // Snackbar on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    val snackbar = Snackbar.make(
                        binding.mainLayout,
                        getString(R.string.default_network_error), Snackbar.LENGTH_INDEFINITE
                    )
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
                searchImeAction(it)
            }
        }
    }

    private fun searchImeAction(searchString: String) {
        val action = MainFragmentDirections.navigateToSearchResults(searchString)
        findNavController().navigate(action)
    }


}