package com.aggarwalankur.tmdbsample.view

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.aggarwalankur.tmdbsample.R
import com.aggarwalankur.tmdbsample.common.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class BindingUtils {
    class ViewBindingAdapter {

        companion object {
            @BindingAdapter("loadImg")
            @JvmStatic
            fun loadPhotoFromUrl(view: ImageView, url: String?) {
                url?.let {
                    val imageUrl = Constants.IMAGE_URL + it
                    Glide.with(view.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_unknown)
                        .error(R.drawable.ic_unknown)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(view)
                }
            }
        }
    }
}