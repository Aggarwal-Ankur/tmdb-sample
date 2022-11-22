package com.aggarwalankur.tmdbsample.view.latest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.aggarwalankur.tmdbsample.R
import com.aggarwalankur.tmdbsample.common.Strings
import com.aggarwalankur.tmdbsample.databinding.LoadStateFooterItemBinding

class MainLoadStateViewHolder(
    private val binding: LoadStateFooterItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    val retryButton = binding.retryButton
        .also {
            it.setOnClickListener { retry() }
        }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = Strings.get(R.string.default_network_error)
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.errorMsg.isVisible = loadState is LoadState.Error
        retryButton.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): MainLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.load_state_footer_item, parent, false)
            val binding = LoadStateFooterItemBinding.bind(view)
            return MainLoadStateViewHolder(binding, retry)
        }
    }
}