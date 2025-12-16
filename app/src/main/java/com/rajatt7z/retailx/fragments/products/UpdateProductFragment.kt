package com.rajatt7z.retailx.fragments.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rajatt7z.retailx.databinding.FragmentUpdateProductBinding
import com.rajatt7z.retailx.models.Product
import com.rajatt7z.retailx.repository.ProductRepository
import kotlinx.coroutines.launch

class UpdateProductFragment : Fragment() {

    private var _binding: FragmentUpdateProductBinding? = null
    private val binding get() = _binding!!
    private val repository = ProductRepository()
    private val args: UpdateProductFragmentArgs by navArgs()
    private lateinit var currentProduct: Product

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProduct(args.productId)

        binding.btnUpdate.setOnClickListener {
            updateProduct()
        }
    }

    private fun loadProduct(id: String) {
        lifecycleScope.launch {
            val product = repository.getProduct(id)
            if (product != null) {
                currentProduct = product
                populateFields(product)
            } else {
                Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun populateFields(product: Product) {
        binding.etProductName.setText(product.name)
        binding.etProductDescription.setText(product.description)
        binding.etProductPrice.setText(product.price.toString())
        binding.etProductStock.setText(product.stock.toString())
        binding.etProductCategory.setText(product.category)
    }

    private fun updateProduct() {
        if (!::currentProduct.isInitialized) return

        currentProduct.name = binding.etProductName.text.toString()
        currentProduct.description = binding.etProductDescription.text.toString()
        currentProduct.price = binding.etProductPrice.text.toString().toDoubleOrNull() ?: 0.0
        currentProduct.stock = binding.etProductStock.text.toString().toIntOrNull() ?: 0
        currentProduct.category = binding.etProductCategory.text.toString()

        lifecycleScope.launch {
            try {
                repository.updateProduct(currentProduct)
                Toast.makeText(context, "Product updated", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } catch (e: Exception) {
                Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
