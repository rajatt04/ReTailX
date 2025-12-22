package com.rajatt7z.retailx.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rajatt7z.retailx.R
import com.rajatt7z.retailx.databinding.ItemProductImageBinding

sealed class ImageItem {
    data class Remote(val url: String) : ImageItem()
    data class Local(val uri: android.net.Uri) : ImageItem()
    data class Placeholder(val color: Int) : ImageItem()
}

class ProductImageAdapter(
    private val items: List<ImageItem>,
    private val isCarousel: Boolean = true
) :
    RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder>() {

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
        if (items.isEmpty()) return
        val actualPosition = position % items.size
        when (val item = items[actualPosition]) {
            is ImageItem.Remote -> {
                holder.binding.imgProduct.load(item.url) {
                    crossfade(true)
                    placeholder(R.mipmap.ic_launcher)
                }
                holder.binding.imgProduct.setBackgroundColor(0) // Reset background
                holder.binding.imgProduct.setPadding(0, 0, 0, 0)
            }
            is ImageItem.Local -> {
                holder.binding.imgProduct.load(item.uri) {
                    crossfade(true)
                    placeholder(R.mipmap.ic_launcher)
                }
                holder.binding.imgProduct.setBackgroundColor(0) // Reset background
                holder.binding.imgProduct.setPadding(0, 0, 0, 0)
            }
            is ImageItem.Placeholder -> {
                holder.binding.imgProduct.setImageResource(R.mipmap.ic_launcher)
                // Use a padding for the icon so it doesn't fill the whole space like a photo
                holder.binding.imgProduct.setPadding(100, 100, 100, 100) 
                holder.binding.imgProduct.setBackgroundColor(item.color)
            }
        }
    }

    override fun getItemCount(): Int = if (isCarousel) (if (items.isEmpty()) 0 else items.size * 500) else items.size
}
