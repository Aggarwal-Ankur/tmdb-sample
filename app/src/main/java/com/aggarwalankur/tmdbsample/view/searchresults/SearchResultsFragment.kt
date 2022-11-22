package com.aggarwalankur.tmdbsample.view.searchresults

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.aggarwalankur.tmdbsample.R
import com.aggarwalankur.tmdbsample.databinding.FragmentSearchResultsBinding
import com.aggarwalankur.tmdbsample.network.Movie
import com.aggarwalankur.tmdbsample.view.latest.ItemViewHolder
import com.aggarwalankur.tmdbsample.view.viewmodels.SearchResultsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchResultsFragment : Fragment(), ItemViewHolder.OnClickListener {
    lateinit var binding: FragmentSearchResultsBinding
        private set

    private lateinit var adapter: MovieListBindingAdapter
    private lateinit var searchString: String

    private val args: SearchResultsFragmentArgs by navArgs()
    private val viewModel: SearchResultsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchResultsBinding.inflate(inflater).apply {
            searchString = args.searchQuery
        }
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)

        binding.bindState()

        return binding.root
    }

    override fun onMovieClick(movie: Movie) {
        val action = SearchResultsFragmentDirections.navigateToDetailsFragment(movie)
        findNavController().navigate(action)
    }

    private fun FragmentSearchResultsBinding.bindState() {
        adapter = MovieListBindingAdapter(this@SearchResultsFragment)
        list.adapter = adapter
        bindList(adapter = adapter)
    }

    private fun bindList(
        adapter: MovieListBindingAdapter
    ) {
        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.getSearchResults()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.searchedMovies.collectLatest {
                adapter.submitList(it.items)
                adapter.notifyDataSetChanged()
                if (it.items.isNotEmpty()) {
                    binding.searchCount.text = String.format(
                        getString(R.string.search_results_text),
                        it.items.size, it.totalCount, searchString
                    )
                } else {
                    binding.searchCount.text = String.format(
                        getString(R.string.no_results_text),
                        searchString
                    )

                }
            }
        }

    }

}