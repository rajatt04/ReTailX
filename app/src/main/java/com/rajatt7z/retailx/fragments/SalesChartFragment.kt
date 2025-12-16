package com.rajatt7z.retailx.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.rajatt7z.retailx.databinding.FragmentSalesChartBinding

class SalesChartFragment : Fragment() {

    private var _binding: FragmentSalesChartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        setupToggleListeners()
        loadChartData(7) // Default to weekly (7 days)
    }

    private fun setupChart() {
        val chart = binding.lineChart
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)
        
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        
        chart.axisLeft.setDrawGridLines(true)
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = true
    }

    private fun setupToggleListeners() {
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.btnDaily.id -> loadChartData(24) // 24 Hours
                    binding.btnWeekly.id -> loadChartData(7) // 7 Days
                    binding.btnMonthly.id -> loadChartData(30) // 30 Days
                }
            }
        }
    }

    private fun loadChartData(count: Int) {
        val entries = ArrayList<Entry>()
        
        // Mock Data Generation
        for (i in 0 until count) {
            val value = (Math.random() * 100).toFloat() + 50
            entries.add(Entry(i.toFloat(), value))
        }

        val dataSet = LineDataSet(entries, "Sales ($)")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setCircleColor(Color.BLUE)
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.CYAN
        dataSet.fillAlpha = 50

        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData
        binding.lineChart.invalidate() // Refresh chart
        binding.lineChart.animateX(1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
