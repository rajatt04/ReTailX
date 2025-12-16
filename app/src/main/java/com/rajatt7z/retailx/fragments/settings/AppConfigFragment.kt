package com.rajatt7z.retailx.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rajatt7z.retailx.databinding.FragmentAppConfigBinding

class AppConfigFragment : Fragment() {

    private var _binding: FragmentAppConfigBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        
        // Mock logic for V1
        binding.switchMaintenance.setOnCheckedChangeListener { _, isChecked ->
             // Save preference
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
