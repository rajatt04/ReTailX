package com.rajatt7z.retailx.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rajatt7z.retailx.databinding.FragmentAdminProfileBinding
import com.rajatt7z.retailx.viewmodel.AuthViewModel
import com.rajatt7z.retailx.utils.Resource
import com.google.firebase.auth.FirebaseAuth

class AdminProfileFragment : Fragment() {

    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        loadData()
        setupListeners()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            binding.etEmail.setText(currentUser.email)
            // Fetch other details if stored in Firestore
            viewModel.fetchUserDetails(currentUser.uid)
        }
        
        viewModel.userDetails.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                val data = resource.data
                binding.etBusinessName.setText(data?.get("businessName") as? String ?: "")
                binding.etPhone.setText(data?.get("phone") as? String ?: "")
            }
        }
    }

    private fun setupListeners() {
        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etBusinessName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            
            if (name.isNotEmpty()) {
                // Implement update logic in ViewModel/Repository if needed. 
                // For now just showing a toast as the updateProfile method might need to be added.
                Toast.makeText(context, "Profile Updated Locally (Implement DB update)", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(context, "Business Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
