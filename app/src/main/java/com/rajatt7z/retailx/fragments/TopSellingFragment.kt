package com.rajatt7z.retailx.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajatt7z.retailx.databinding.FragmentTopSellingBinding

class TopSellingFragment : Fragment() {

    private var _binding: FragmentTopSellingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopSellingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvTopSelling.layoutManager = LinearLayoutManager(context)
        // TODO: Attach Adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
