package com.aggarwalankur.tmdbsample.view.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.aggarwalankur.tmdbsample.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment() {
    lateinit var binding: FragmentDetailsBinding
        private set

    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false).apply {
            movie = args.selectedMovie
        }

        return binding.root
    }

}