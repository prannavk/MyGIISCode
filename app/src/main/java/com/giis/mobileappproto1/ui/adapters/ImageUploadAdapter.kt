package com.giis.mobileappproto1.ui.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.giis.mobileappproto1.databinding.LayoutUploadedImageBinding
import com.giis.mobileappproto1.utils.globalTag

class ImageUploadAdapter(
    private val context: Context,
    private val imageUrisList: MutableList<Uri?>,
    private val actionOnMediaDequeue: (position: Int) -> Unit
) : RecyclerView.Adapter<ImageUploadAdapter.UploadedImageVH>() {

    inner class UploadedImageVH(private val binding: LayoutUploadedImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri, position: Int) {
            binding.uploadedImageIvTile.setImageURI(uri)
            binding.imageTileRemoveBtn.setOnClickListener {
                actionOnMediaDequeue.invoke(position)
                // this@ImageUploadAdapter.imageUrisList.removeAt(position)
                // On Media Deletion
                this@ImageUploadAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadedImageVH {
        val inflater = LayoutInflater.from(context)
        val binding = LayoutUploadedImageBinding.inflate(inflater, parent, false)
        return UploadedImageVH(binding)
    }

    override fun getItemCount(): Int = imageUrisList.size

    override fun onBindViewHolder(holder: UploadedImageVH, position: Int) {
        val uriDataAtPos = imageUrisList[position]
        uriDataAtPos?.let { holder.bind(it, position) } ?: run {
            Log.e(
                globalTag,
                "NULL URI obtained for Picked Image"
            )
        }
    }
}