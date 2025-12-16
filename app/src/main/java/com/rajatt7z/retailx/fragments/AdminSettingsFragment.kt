package com.rajatt7z.retailx.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rajatt7z.retailx.auth.AdminLogin
import com.rajatt7z.retailx.databinding.FragmentAdminSettingsBinding
import com.rajatt7z.retailx.viewmodel.AuthViewModel

class AdminSettingsFragment : Fragment() {

    private var _binding: FragmentAdminSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        binding.cardProfile.setOnClickListener {
            findNavController().navigate(com.rajatt7z.retailx.R.id.action_settings_to_profile)
        }
        
        binding.cardPassword.setOnClickListener {
             findNavController().navigate(com.rajatt7z.retailx.R.id.action_settings_to_password)
        }
        
        binding.cardAppConfig.setOnClickListener {
             findNavController().navigate(com.rajatt7z.retailx.R.id.action_settings_to_config)
        }
        
        binding.cardRBAC.setOnClickListener {
             findNavController().navigate(com.rajatt7z.retailx.R.id.action_settings_to_rbac)
        }
        
        binding.cardLogs.setOnClickListener {
             findNavController().navigate(com.rajatt7z.retailx.R.id.action_settings_to_logs)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
            requireActivity().startActivity(Intent(requireContext(), AdminLogin::class.java))
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
