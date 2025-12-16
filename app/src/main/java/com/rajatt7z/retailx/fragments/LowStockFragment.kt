package com.rajatt7z.retailx.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajatt7z.retailx.databinding.FragmentLowStockBinding

class LowStockFragment : Fragment() {

    private var _binding: FragmentLowStockBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLowStockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvLowStock.layoutManager = LinearLayoutManager(context)
        // TODO: Attach Adapter with Mock Data
        // For now, just show empty or mock items later
        binding.tvEmpty.visibility = View.VISIBLE
        binding.tvEmpty.text = "No low stock items (Mock)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
