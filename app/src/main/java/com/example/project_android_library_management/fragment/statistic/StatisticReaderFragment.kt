package com.example.project_android_library_management.fragment.statistic

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.StatisticDao
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticReaderFragment : Fragment() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var statisticDao: StatisticDao
    private lateinit var readerByGenderPieChart: PieChart
    private lateinit var readerMostBorrowedBarChart: BarChart
    private lateinit var bookCategoryBarChart: BarChart

    private lateinit var tvTotalReader: TextView
    private lateinit var tvReaderBorrow: TextView
    private lateinit var tvReaderReturn: TextView
    private lateinit var tvReaderNew: TextView

    companion object {
        private const val ARG_TAB = "arg_tab"
        private const val ARG_FROM_DATE = "arg_from_date"
        private const val ARG_TO_DATE = "arg_to_date"

        fun newInstance(tab: String, fromDate: String? = null, toDate: String? = null): StatisticReaderFragment {
            val fragment = StatisticReaderFragment()
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
        val view = inflater.inflate(R.layout.fragment_statistic_reader, container, false)

        databaseHelper = DatabaseHelper(requireContext())
        statisticDao = StatisticDao(databaseHelper)

        tvTotalReader = view.findViewById(R.id.tvTotalReader)
        tvReaderBorrow = view.findViewById(R.id.tvReaderBorrow)
        tvReaderReturn = view.findViewById(R.id.tvReaderReturn)
        tvReaderNew = view.findViewById(R.id.tvReaderNew)

        val selectedTab = arguments?.getString(ARG_TAB)
        var fromDate = arguments?.getString(ARG_FROM_DATE)
        var toDate = arguments?.getString(ARG_TO_DATE)

        if (fromDate == null || toDate == null) {
            val dates = when (selectedTab) {
                "Ngày" -> getTodayDates()
                "Tháng" -> getCurrentMonthDates()
                "Quý" -> getCurrentQuarterDates()
                "Năm" -> getCurrentYearDates()
                "Tùy chỉnh" -> null
                else -> null
            }
            if (dates != null) {
                fromDate = dates.first
                toDate = dates.second
            }
        }

        loadQuantityReader(fromDate, toDate)

        readerByGenderPieChart = view.findViewById(R.id.readerByGenderPieChart)
        setupReaderByGenderPieChart()

        readerMostBorrowedBarChart = view.findViewById(R.id.readerMostBorrowedBarChart)
        setupBarChart(fromDate, toDate)

        return view
    }

    private fun loadQuantityReader(fromDate: String?, toDate: String?) {
        val totalReader = statisticDao.getTotalReader()
        val readerBorrow = statisticDao.getReaderBorrow(fromDate, toDate)
        val readerReturn = statisticDao.getReaderReturn(fromDate, toDate)
        val readerNew = statisticDao.getReaderNew(fromDate, toDate)

        tvTotalReader.text = totalReader.toString()
        tvReaderBorrow.text = readerBorrow.toString()
        tvReaderReturn.text = readerReturn.toString()
        tvReaderNew.text = readerNew.toString()
    }

    private fun setupReaderByGenderPieChart() {
        val readerCounts = statisticDao.getReaderCountByGeander()
        val entries = mutableListOf<PieEntry>()

        readerCounts.forEach { (gender, count) ->
            entries.add(PieEntry(count.toFloat(), gender))
        }

        if (entries.isEmpty()) {
            Toast.makeText(readerByGenderPieChart.context, "Không có dữ liệu để hiển thị", Toast.LENGTH_SHORT).show()
            return
        }

        val dataSet = PieDataSet(entries, "")
        val colorList = listOf(
            ContextCompat.getColor(requireContext(), R.color.secondary),
            ContextCompat.getColor(requireContext(), R.color.secondary_variant),
            ContextCompat.getColor(requireContext(), R.color.third)
        )
        dataSet.colors = colorList
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.oswald)
        dataSet.valueTypeface = typeface
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_dark_color)

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val total = readerByGenderPieChart.data?.getYValueSum() ?: 1f
                val percentage = (value / total) * 100
                return "${value.toInt()} (${String.format("%.2f", percentage)}%)"
            }
        }

        val pieData = PieData(dataSet)
        readerByGenderPieChart.data = pieData

        customPieChart(readerByGenderPieChart)

        readerByGenderPieChart.invalidate()
    }

    private fun customPieChart(pieChart: PieChart) {
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
    }

    private fun setupBarChart(fromDate: String?, toDate: String?) {
        val readerMostBorrowed = statisticDao.getTop5ReaderMostBorrowed(fromDate, toDate)

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        var index = 0
        readerMostBorrowed.forEach { (readerName, count) ->
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
            labels.add(readerName)
            index++
        }

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
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
                val typeface = ResourcesCompat.getFont(requireContext(), R.font.oswald)
                valueTypeface = typeface
            }
        }

        val barData = BarData(dataSet)
        readerMostBorrowedBarChart.data = barData

        customBarChart(readerMostBorrowedBarChart)

        readerMostBorrowedBarChart.invalidate()
    }

    private fun customBarChart(barChart: BarChart) {
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
            setDrawInside(false)
            setWordWrapEnabled(true)
        }

        barChart.description.isEnabled = false
    }

    private fun getTodayDates(): Pair<String, String> {
        val today = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = formatter.format(today.time)
        return formattedDate to formattedDate
    }

    private fun getCurrentMonthDates(): Pair<String, String> {
        val today = Calendar.getInstance()
        val startOfMonth = today.clone() as Calendar
        val endOfMonth = today.clone() as Calendar
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH))
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(startOfMonth.time) to formatter.format(endOfMonth.time)
    }

    private fun getCurrentQuarterDates(): Pair<String, String> {
        val today = Calendar.getInstance()
        val currentMonth = today.get(Calendar.MONTH) + 1
        val startMonth = when (currentMonth) {
            in 1..3 -> 0
            in 4..6 -> 3
            in 7..9 -> 6
            else -> 9
        }
        val endMonth = startMonth + 2
        val startOfQuarter = today.clone() as Calendar
        val endOfQuarter = today.clone() as Calendar
        startOfQuarter.set(Calendar.MONTH, startMonth)
        startOfQuarter.set(Calendar.DAY_OF_MONTH, 1)
        endOfQuarter.set(Calendar.MONTH, endMonth)
        endOfQuarter.set(Calendar.DAY_OF_MONTH, endOfQuarter.getActualMaximum(Calendar.DAY_OF_MONTH))
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(startOfQuarter.time) to formatter.format(endOfQuarter.time)
    }

    private fun getCurrentYearDates(): Pair<String, String> {
        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)
        val startOfYear = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val endOfYear = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 31)
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(startOfYear.time) to formatter.format(endOfYear.time)
    }
}