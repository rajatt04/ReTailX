package com.rajatt7z.retailx.fragments.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rajatt7z.retailx.databinding.FragmentAddProductBinding
import com.rajatt7z.retailx.models.Product
import com.rajatt7z.retailx.repository.ProductRepository
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private val repository = ProductRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            saveProductAndProceed()
        }
    }

    private fun saveProductAndProceed() {
        val name = binding.etProductName.text.toString()
        val desc = binding.etProductDescription.text.toString()
        val priceStr = binding.etProductPrice.text.toString()
        val stockStr = binding.etProductStock.text.toString()
        val category = binding.etProductCategory.text.toString()

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val product = Product(
            name = name,
            description = desc,
            price = priceStr.toDouble(),
            stock = stockStr.toInt(),
            category = category
        )

        lifecycleScope.launch {
            try {
                val productId = repository.addProduct(product)
                val action = AddProductFragmentDirections.actionAddProductFragmentToProductImageUploadFragment(productId)
                findNavController().navigate(action)
            } catch (e: Exception) {
                Toast.makeText(context, "Error saving product: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
