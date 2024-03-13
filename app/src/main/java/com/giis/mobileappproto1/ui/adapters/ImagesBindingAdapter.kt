package com.giis.mobileappproto1.ui.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.giis.mobileappproto1.R

class ImagesBindingAdapter {
    companion object {

        @BindingAdapter("imageFromUrl")
        @JvmStatic
        fun ImageView.imageFromUrl(url: String) {
            Glide.with(this.context)
                .load(url)
                .error(R.drawable.bookpng)
                .placeholder(R.drawable.bookpng)
                .fitCenter()
                .into(this)

        }
    }
}