package com.rajatt7z.retailx.fragments.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rajatt7z.retailx.databinding.FragmentAddProductBinding
import com.rajatt7z.retailx.models.Product
import com.rajatt7z.retailx.repository.ProductRepository
import com.rajatt7z.retailx.database.AppDatabase
import com.rajatt7z.retailx.database.DraftProduct
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
        setupCategoryDropdown()
        setupClickListeners()
    }

    private fun setupCategoryDropdown() {
        val categories = listOf(
            "Electronics",
            "Clothing",
            "Home & Living",
            "Beauty & Personal Care",
            "Sports & Outdoors",
            "Toys & Games",
            "Books & Stationery",
            "Automotive",
            "Groceries",
            "Health & Wellness",
            "Others"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.etProductCategory.setAdapter(adapter)
    }

    private fun setupClickListeners() {
        binding.apply {
            btnNext.setOnClickListener {
                saveProductAndProceed()
            }

            btnSaveDraft.setOnClickListener {
                saveAsDraft()
            }
        }
    }

    private fun saveProductAndProceed() {
        if (!validateInputs()) return

        val product = createProductFromInput()

        lifecycleScope.launch {
            try {
                setLoading(true)
                val productId = repository.addProduct(product)
                setLoading(false)
                val action = AddProductFragmentDirections.actionAddProductFragmentToProductImageUploadFragment(productId)
                findNavController().navigate(action)
            } catch (e: Exception) {
                setLoading(false)
                Toast.makeText(requireContext(), "Error saving product: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun saveAsDraft() {
        val name = binding.etProductName.text.toString()
        if (name.isEmpty()) {
            binding.etProductName.error = "Name is required for draft"
            return
        }
        
        val product = createProductFromInput()
        val draft = DraftProduct(
            name = product.name,
            description = product.description,
            price = product.price,
            stock = product.stock,
            category = product.category
        )

        lifecycleScope.launch {
            try {
                setLoading(true)
                AppDatabase.getDatabase(requireContext())
                    .draftProductDao()
                    .insertDraft(draft)
                setLoading(false)
                Toast.makeText(requireContext(), "Draft saved successfully", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } catch (e: Exception) {
                setLoading(false)
                Toast.makeText(requireContext(), "Error saving draft: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        binding.apply {
            val name = etProductName.text.toString()
            val priceStr = etProductPrice.text.toString()
            val stockStr = etProductStock.text.toString()
            val category = etProductCategory.text.toString()

            if (name.isEmpty()) {
                etProductName.error = "Name is required"
                return false
            }
            if (priceStr.isEmpty()) {
                etProductPrice.error = "Price is required"
                return false
            }
            if (stockStr.isEmpty()) {
                etProductStock.error = "Stock is required"
                return false
            }
             if (category.isEmpty()) {
                etProductCategory.error = "Category is required"
                return false
            }
        }
        return true
    }

    private fun createProductFromInput(): Product {
        binding.apply {
            return Product(
                name = etProductName.text.toString().trim(),
                description = etProductDescription.text.toString().trim(),
                price = etProductPrice.text.toString().toDoubleOrNull() ?: 0.0,
                stock = etProductStock.text.toString().toIntOrNull() ?: 0,
                category = etProductCategory.text.toString()
            )
        }
    }
    
    private fun setLoading(isLoading: Boolean) {
        binding.btnNext.isEnabled = !isLoading
        binding.btnSaveDraft.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
