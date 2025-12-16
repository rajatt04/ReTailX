package com.rajatt7z.retailx.fragments.products

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rajatt7z.retailx.databinding.FragmentProductImageUploadBinding
import com.rajatt7z.retailx.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductImageUploadFragment : Fragment() {

    private var _binding: FragmentProductImageUploadBinding? = null
    private val binding get() = _binding!!
    private val repository = ProductRepository()
    private val args: ProductImageUploadFragmentArgs by navArgs()
    private val selectedImages = mutableListOf<Uri>()

    private val pickImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            if (selectedImages.size + uris.size > 5) {
                Toast.makeText(context, "Max 5 images allowed", Toast.LENGTH_SHORT).show()
            } else {
                selectedImages.addAll(uris)
                // In a real app, update a RecyclerView or GridView to show selected images
                Toast.makeText(context, "Added ${uris.size} images", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductImageUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSelectImages.setOnClickListener {
             pickImagesLauncher.launch("image/*")
        }

        binding.btnUpload.setOnClickListener {
            uploadImages()
        }
    }

    private fun uploadImages() {
        if (selectedImages.isEmpty()) {
            Toast.makeText(context, "No images selected", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnUpload.isEnabled = false

        lifecycleScope.launch {
            try {
                val uploadedUrls = mutableListOf<String>()
                for (uri in selectedImages) {
                    val url = repository.uploadImage(requireContext(), uri)
                    uploadedUrls.add(url)
                }
                
                // Update product with URLs
                val product = repository.getProduct(args.productId)
                if (product != null) {
                    product.imageUrls = uploadedUrls
                    repository.updateProduct(product)
                    Toast.makeText(context, "Images uploaded successfully", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack(com.rajatt7z.retailx.R.id.productListFragment, false)
                }
            } catch (e: Exception) {
                android.util.Log.e("UploadError", "Error uploading images", e)
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Upload Failed")
                    .setMessage(e.toString())
                    .setPositiveButton("OK", null)
                    .show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnUpload.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
