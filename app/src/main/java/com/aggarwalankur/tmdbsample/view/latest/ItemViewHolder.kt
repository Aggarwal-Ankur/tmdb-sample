package com.aggarwalankur.tmdbsample.view.latest

import androidx.recyclerview.widget.RecyclerView
import com.aggarwalankur.tmdbsample.databinding.ListItemMovieBinding
import com.aggarwalankur.tmdbsample.network.Movie
import javax.inject.Inject

class ItemViewHolder @Inject constructor(
    private val binding: ListItemMovieBinding,
    private val onClickListener: OnClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie) {
        binding.movie = movie

        binding.root.setOnClickListener {
            onClickListener.onMovieClick(movie)
        }
    }

    interface OnClickListener {
        fun onMovieClick(movie: Movie)
    }
}