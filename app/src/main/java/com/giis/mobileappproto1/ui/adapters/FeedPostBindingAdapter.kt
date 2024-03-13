package com.giis.mobileappproto1.ui.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.data.models.DimensionsBindingUtil
import com.giis.mobileappproto1.data.models.FeedPostDTO
import com.giis.mobileappproto1.utils.globalTag
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel

class FeedPostBindingAdapter {
    companion object {

        @BindingAdapter("fetchImagesFromTheseUrls", "referenceSizes", requireAll = true)
        @JvmStatic
        fun com.google.android.flexbox.FlexboxLayout.fetchImagesFromTheseUrls(
            post: FeedPostDTO, sizeUtil: DimensionsBindingUtil
        ) {
            fun findSquaresCount(images: MutableList<com.google.android.material.imageview.ShapeableImageView>): Int {
                var ct = 0
                for (image in images) {
                    if (image.width == sizeUtil.unitSquareSide && image.height == sizeUtil.unitSquareSide) {
                        ct += 1
                    }
                }
                return ct
            }

            fun checkIfRectangleExists(images: MutableList<com.google.android.material.imageview.ShapeableImageView>): Boolean {
                var exists = false
                images.forEach { exists = it.width == sizeUtil.unitRectangleWidth }
                return exists
            }

            fun sortSizes(images: MutableList<com.google.android.material.imageview.ShapeableImageView>) {
                images.sortByDescending { it.width }.also { Log.e(globalTag, images.toString()) }
            }

            if (post.postImageStatus && post.postImageList != null) {

                var excessImagesFlag = false
                var fitWidth = 0
                var fitHeight = 0
                val fetchedImages: MutableList<com.google.android.material.imageview.ShapeableImageView> =
                    mutableListOf()

                val only5UrlsList: List<String> = if (post.postImageList.size > 5) {
                    post.postImageList.subList(0, 5).also {
                        excessImagesFlag = true
                    }
                } else {
                    post.postImageList
                }

                for (postImageUrl in only5UrlsList) {

                    val imageView =
                        with(com.google.android.material.imageview.ShapeableImageView(this.context)) {
                            id = View.generateViewId()
                            layoutParams =
                                ViewGroup.LayoutParams(
                                    sizeUtil.unitRectangleWidth,
                                    sizeUtil.unitRectangleHeight
                                )
                            val shapeAppearanceModel = ShapeAppearanceModel.builder()
                                .setAllCorners(CornerFamily.ROUNDED, 18f)
                                .build()
                            setShapeAppearanceModel(shapeAppearanceModel)
                            this
                        }

                    Glide.with(this.context)
                        .load(postImageUrl)
                        .error(R.drawable.bookpng)
                        .fitCenter()
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.bookpng)
                        .addListener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean =
                                false.also { Log.e(globalTag, "Failure in Image fetching") }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean = false.also {
                                fitWidth = resource!!.intrinsicWidth
                                fitHeight = resource.intrinsicHeight
                                Log.e(
                                    globalTag,
                                    "Image fetching Success, Also: $fitWidth and $fitHeight are available"
                                )
                                if (fitWidth <= fitHeight) {
                                    val layoutParams = imageView.layoutParams
                                    layoutParams.width = sizeUtil.unitSquareSide
                                    layoutParams.height = sizeUtil.unitSquareSide
                                    imageView.layoutParams = layoutParams
                                }
                            }
                        })
                        .into(imageView)

                    fetchedImages.add(imageView)

                }

                fun configureImageViews(): MutableList<com.google.android.material.imageview.ShapeableImageView> {
                    when (fetchedImages.size) {
                        1 -> {
                            val onlyImage = fetchedImages[0]
                            val p = onlyImage.layoutParams
                            p.width = sizeUtil.maxRectangleWidth
                            p.height = sizeUtil.maxRectangleHeight
                            onlyImage.layoutParams = p
                            return mutableListOf(onlyImage)
                        }

                        2 -> {
                            val tempImages =
                                mutableListOf<com.google.android.material.imageview.ShapeableImageView>()
                            for (image in fetchedImages) {
                                val p = image.layoutParams
                                p.width = sizeUtil.unitRectangleWidth
                                p.height = sizeUtil.unitRectangleHeight
                                image.layoutParams = p
                                tempImages.add(image)
                            }
                            return tempImages
                        }

                        3 -> {
                            if (checkIfRectangleExists(fetchedImages)) {
                                sortSizes(fetchedImages)
                                val tempImages =
                                    mutableListOf<com.google.android.material.imageview.ShapeableImageView>()
                                fetchedImages.forEachIndexed { index, imageView ->
                                    val p = imageView.layoutParams
                                    p.width =
                                        if (index == 0) sizeUtil.maxRectangleWidth else sizeUtil.unitRectangleWidth
                                    p.height =
                                        if (index == 0) sizeUtil.maxRectangleHeight else sizeUtil.unitRectangleHeight
                                    imageView.layoutParams = p
                                    tempImages.add(imageView)
                                }
                                return tempImages
                            } else {
                                val tempImages =
                                    mutableListOf<com.google.android.material.imageview.ShapeableImageView>()
                                fetchedImages.forEachIndexed { index, imageView ->
                                    val p = imageView.layoutParams
                                    p.width = sizeUtil.unitSquareSide
                                    p.height = sizeUtil.unitSquareSide
                                    imageView.layoutParams = p
                                    tempImages.add(imageView)
                                }
                                return tempImages
                            }
                        }

                        4 -> {
                            if (findSquaresCount(fetchedImages) == 3) {
                                sortSizes(fetchedImages)
                                val tempImages =
                                    mutableListOf<com.google.android.material.imageview.ShapeableImageView>()
                                fetchedImages.forEachIndexed { index, image ->
                                    val p = image.layoutParams
                                    p.width =
                                        if (index == 0) sizeUtil.unitRectangleWidth else sizeUtil.unitSquareSide
                                    p.height =
                                        if (index == 0) sizeUtil.unitRectangleHeight else sizeUtil.unitSquareSide
                                    image.layoutParams = p
                                    tempImages.add(image)
                                }
                                return tempImages
                            } else {
                                val tempImages =
                                    mutableListOf<com.google.android.material.imageview.ShapeableImageView>()
                                for (image in fetchedImages) {
                                    val p = image.layoutParams
                                    p.width = sizeUtil.unitRectangleWidth
                                    p.height = sizeUtil.unitRectangleHeight
                                    image.layoutParams = p
                                    tempImages.add(image)
                                }
                                return tempImages
                            }
                        }

                        5 -> {
                            sortSizes(fetchedImages)
                            val tempImages =
                                mutableListOf<com.google.android.material.imageview.ShapeableImageView>()
                            fetchedImages.forEachIndexed { index, imageView ->
                                val p = imageView.layoutParams
                                if (index >= 2) {
                                    p.width = sizeUtil.unitSquareSide
                                    p.height = sizeUtil.unitSquareSide
                                } else {
                                    p.width = sizeUtil.unitRectangleWidth
                                    p.height = sizeUtil.unitRectangleHeight
                                }
                                imageView.layoutParams = p
                                tempImages.add(imageView)
                            }
                            return tempImages
                        }

                        else -> return fetchedImages
                    }
                }

                val setImages: MutableList<com.google.android.material.imageview.ShapeableImageView> =
                    configureImageViews()

                // for loop to add it to the layout and check for the flag
                if (setImages.size == 0) {
                    Log.e(globalTag, "There has been an error")
                }
                if (excessImagesFlag) {
                    Log.e(globalTag, "This post has more than 5 images")
                    setImages.forEach { this.addView(it) }
                } else {
                    setImages.forEach { this.addView(it) }
                }


            } else {
                this.visibility = View.GONE
            }
        }

    }
}