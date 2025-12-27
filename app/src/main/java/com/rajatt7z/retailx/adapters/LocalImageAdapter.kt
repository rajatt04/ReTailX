package com.rajatt7z.retailx.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rajatt7z.retailx.R
import com.rajatt7z.retailx.databinding.ItemProductImageBinding

class LocalImageAdapter(
    private val images: List<Uri>
) : RecyclerView.Adapter<LocalImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemProductImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uri = images[position]
        holder.binding.imgProduct.load(uri) {
            crossfade(true)
            placeholder(R.mipmap.ic_launcher)
        }
    }

    override fun getItemCount(): Int = images.size
}
