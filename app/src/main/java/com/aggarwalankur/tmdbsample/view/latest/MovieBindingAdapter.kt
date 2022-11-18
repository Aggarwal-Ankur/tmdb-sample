package com.aggarwalankur.tmdbsample.view.latest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.aggarwalankur.tmdbsample.databinding.ListItemMovieBinding
import com.aggarwalankur.tmdbsample.network.Movie

class MovieBindingAdapter (private val onClickListener: ItemViewHolder.OnClickListener) :
    PagingDataAdapter<Movie, ItemViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ListItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem == newItem
        }
    }
}