package com.rajatt7z.retailx.fragments.products

import android.graphics.Color
import com.google.android.material.carousel.CarouselLayoutManager
import com.rajatt7z.retailx.adapters.ImageItem
import com.rajatt7z.retailx.adapters.ProductImageAdapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rajatt7z.retailx.databinding.FragmentProductDetailsBinding
import com.rajatt7z.retailx.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductDetailsFragment : Fragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private val repository = ProductRepository()
    private val args: ProductDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProduct(args.productId)

        binding.btnEdit.setOnClickListener {
            val action = ProductDetailsFragmentDirections.actionProductDetailsFragmentToUpdateProductFragment(args.productId)
            findNavController().navigate(action)
        }

        binding.btnDelete.setOnClickListener {
            deleteProduct()
        }
    }

    private fun loadProduct(id: String) {
        lifecycleScope.launch {
            val product = repository.getProduct(id)
            if (product != null) {
                binding.tvProductName.text = product.name
                binding.tvProductPrice.text = "$${product.price}"
                binding.tvProductStock.text = "Stock: ${product.stock}"
                binding.tvProductCategory.text = "Category: ${product.category}"
                binding.tvProductDescription.text = product.description
                
                val items = if (product.imageUrls.isNotEmpty()) {
                    product.imageUrls.map { ImageItem.Remote(it) }
                } else {
                    listOf(
                        ImageItem.Placeholder(Color.parseColor("#FFB3BA")), // Pastel Red
                        ImageItem.Placeholder(Color.parseColor("#FFDFBA")), // Pastel Orange
                        ImageItem.Placeholder(Color.parseColor("#FFFFBA")), // Pastel Yellow
                        ImageItem.Placeholder(Color.parseColor("#BAFFC9")), // Pastel Green
                        ImageItem.Placeholder(Color.parseColor("#BAE1FF"))  // Pastel Blue
                    )
                }
                
                val adapter = ProductImageAdapter(items)
                binding.rvProductImages.adapter = adapter
                binding.rvProductImages.layoutManager = CarouselLayoutManager()
                
                // Start in the middle for infinite scrolling effect
                // Using a large finite number (e.g. items.size * 500) prevents OOM in CarouselLayoutManager
                val totalItems = adapter.itemCount
                val middle = totalItems / 2
                val startPosition = middle - (middle % items.size)
                binding.rvProductImages.scrollToPosition(startPosition)
            } else {
                Toast.makeText(context, "Product unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteProduct() {
        lifecycleScope.launch {
            try {
                repository.deleteProduct(args.productId)
                Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } catch (e: Exception) {
                Toast.makeText(context, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
