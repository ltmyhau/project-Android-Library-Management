package com.example.project_android_library_management.fragment.statistic

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.project_android_library_management.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class StatisticBookFragment : Fragment() {
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart

    companion object {
        private const val ARG_TAB = "arg_tab"
        private const val ARG_FROM_DATE = "arg_from_date"
        private const val ARG_TO_DATE = "arg_to_date"

        fun newInstance(tab: String, fromDate: String? = null, toDate: String? = null): StatisticBookFragment {
            val fragment = StatisticBookFragment()
            val args = Bundle().apply {
                putString(ARG_TAB, tab)
                putString(ARG_FROM_DATE, fromDate)
                putString(ARG_TO_DATE, toDate)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistic_book, container, false)

        val selectedTab = arguments?.getString(ARG_TAB)
        val fromDate = arguments?.getString(ARG_FROM_DATE)
        val toDate = arguments?.getString(ARG_TO_DATE)

        pieChart = view.findViewById(R.id.pieChart)
        setupPieChart()

        barChart = view.findViewById(R.id.barChart)
        setupBarChart(view)

        return view
    }

    private fun setupPieChart() {
        val entries = mutableListOf<PieEntry>()
        entries.add(PieEntry(10f, "Đang mượn"))
        entries.add(PieEntry(5f, "Đã trả"))
        entries.add(PieEntry(8f, "Quá hạn"))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.secondary),
            ContextCompat.getColor(requireContext(), R.color.secondary_variant),
            ContextCompat.getColor(requireContext(), R.color.third)
        )
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.oswald)
        dataSet.valueTypeface = typeface
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_dark_color)

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val total = pieChart.data?.getYValueSum() ?: 1f
                val percentage = (value / total) * 100
                return "${value.toInt()} (${String.format("%.2f", percentage)}%)"
            }
        }

        val pieData = PieData(dataSet)
        pieChart.data = pieData

        pieChart.setHoleRadius(0f)
        pieChart.setTransparentCircleRadius(0f)
        pieChart.setHoleColor(Color.TRANSPARENT)

        pieChart.legend.apply {
            isEnabled = true
            form = Legend.LegendForm.SQUARE
            formSize = 10f
            textSize = 14f
            textColor = ContextCompat.getColor(requireContext(), R.color.text_dark_color)
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            setXEntrySpace(20f)
            setYEntrySpace(10f)
            setWordWrapEnabled(true)
        }

        pieChart.description.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.setDrawSliceText(false)

        pieChart.invalidate()
    }

    private fun setupBarChart(view: View) {
        val barChart = view.findViewById<BarChart>(R.id.barChart)

        val entries = listOf(
            BarEntry(0f, 20f),
            BarEntry(1f, 10f),
            BarEntry(2f, 5f),
            BarEntry(3f, 8f),
            BarEntry(4f, 15f)
        )

        val labels = listOf(
            "Đắc nhân tâm",
            "Nhà giả kim",
            "Mặc kệ thiên hạ, sống như người Nhật",
            "Tuổi trẻ đáng giá bao nhiêu?",
            "Điều kỳ diệu của tiệm tạp hóa Namiya"
        )

        val colorList = listOf(
            ContextCompat.getColor(requireContext(), R.color.primary_variant),
            ContextCompat.getColor(requireContext(), R.color.primary),
            ContextCompat.getColor(requireContext(), R.color.secondary),
            ContextCompat.getColor(requireContext(), R.color.secondary_variant),
            ContextCompat.getColor(requireContext(), R.color.third)
        )

        val dataSet = entries.mapIndexed { index, entry ->
            BarDataSet(listOf(entry), labels[index]).apply {
                colors = listOf(colorList[index])
                valueTextSize = 14f
                val typeface = ResourcesCompat.getFont(requireContext(), R.font.oswald)
                valueTypeface = typeface
            }
        }

        val barData = BarData(dataSet)
        barChart.data = barData

        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return ""
                }
            }
            textSize = 12f
            setDrawGridLines(false)
        }

        barChart.axisLeft.apply {
            textSize = 12f
            setDrawGridLines(false)
        }

        barChart.axisRight.isEnabled = false

        barChart.legend.apply {
            isEnabled = true
            form = Legend.LegendForm.SQUARE
            formSize = 8f
            textSize = 12f
            textColor = ContextCompat.getColor(requireContext(), R.color.text_dark_color)
            orientation = Legend.LegendOrientation.VERTICAL
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            setDrawInside(false)
            setWordWrapEnabled(true)
        }


        barChart.description.isEnabled = false
        barChart.invalidate()
    }
}